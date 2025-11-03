package org.kiteio.punica.mirror.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.plugin
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.encodedPath
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.kiteio.punica.mirror.modal.education.Semester
import org.kiteio.punica.mirror.modal.secondclass.Activity
import org.kiteio.punica.mirror.modal.secondclass.BasicActivity
import org.kiteio.punica.mirror.modal.secondclass.Grade
import org.kiteio.punica.mirror.modal.secondclass.GradeLog
import org.kiteio.punica.mirror.modal.secondclass.GradeLogs
import org.kiteio.punica.mirror.modal.secondclass.Grades
import org.kiteio.punica.mirror.util.Json
import org.kiteio.punica.mirror.util.from
import org.kiteio.punica.mirror.util.now
import org.kiteio.punica.mirror.util.parseIsoVariant
import org.kiteio.punica.mirror.util.parseIsoVariantWithoutSecond

/**
 * 第二课堂服务。
 */
fun getSecondClassService(): SecondClassService {
    val httpClient = HttpClient {
        defaultRequest {
            url(SecondClassServiceImpl.BASE_URL)
        }
        install(ContentNegotiation) {
            json(Json)
        }
    }

    return SecondClassServiceImpl(httpClient)
}

/**
 * 第二课堂服务。
 */
interface SecondClassService {
    /**
     * 登录。
     *
     * @param userId 学号
     * @param password 第二课堂密码
     */
    suspend fun login(userId: String, password: String)

    /**
     * 成绩单。
     */
    suspend fun getGrades(): Grades

    /**
     * 成绩日志。
     */
    suspend fun getGradeLogs(): GradeLogs

    /**
     * 活动列表。
     */
    suspend fun getActivities(): List<BasicActivity>

    /**
     * 我的活动列表。
     */
    suspend fun getMyActivities(
        state: BasicActivity.State,
    ): List<BasicActivity>

    /**
     * 活动详情。
     *
     * @param id 活动唯一标识
     */
    suspend fun getActivity(id: Int): Activity
}

// --------------- 实现 ---------------

private class SecondClassServiceImpl(
    private val httpClient: HttpClient,
) : SecondClassService {
    private var user: SecondClassUser? = null

    init {
        httpClient.plugin(HttpSend).intercept { builder ->
            return@intercept if (builder.url.encodedPath == LOGIN_URL) {
                // 登录时保存 token
                execute(builder).also { call ->
                    user = user?.copy(
                        token = call.response.headers["X-Token"]
                    )
                }
            } else {
                // 请求时设置 token
                builder.header("X-Token", user?.token)
                execute(builder)
            }
        }
    }

    override suspend fun login(userId: String, password: String) {
        user = SecondClassUser(userId)

        val params = Json.encodeToString(
            mapOf(
                "school" to "10018",
                "account" to userId,
                "password" to password.ifEmpty { userId },
            )
        )
        val body = httpClient.submitForm(
            LOGIN_URL,
            parameters {
                append("para", params)
            }
        ).body<Response<LoginData>>()

        user = user?.copy(systemId = body.data.id)
    }

    /**
     * 登录响应数据。
     */
    @Serializable
    private data class LoginData(val id: String)

    override suspend fun getGrades(): Grades {
        require(user != null)
        val body = httpClient.get(
            "/apps/user/achievement/by-classify-list",
        ) {
            parameter(
                "para",
                Json.encodeToString(
                    mapOf("userId" to user!!.systemId),
                ),
            )
        }.body<Response<List<GradeData>>>()

        val grades = body.data.map {
            Grade(
                name = it.name,
                score = it.score,
                requiredScore = it.requiredScore,
            )
        }

        return Grades(
            userId = user!!.id,
            createAt = LocalDate.now(),
            grades = grades,
        )
    }

    /**
     * 成绩单响应数据。
     *
     * @property name 分类名称
     * @property score 成绩
     * @property requiredScore 成绩要求
     */
    @Serializable
    private data class GradeData(
        @SerialName("classifyName") val name: String,
        @SerialName("classifyHours") val score: Double,
        @SerialName("classifySchoolMinHours")
        val requiredScore: Double,
    )

    override suspend fun getGradeLogs(): GradeLogs {
        val body = httpClient.get(
            "/apps/user/achievement/by-classify-list-detail",
        ).body<Response<List<GradeLogData>>>()

        val logs = body.data.map { gradeLogData ->
            GradeLog(
                activityName = gradeLogData.activityName,
                category = gradeLogData.category,
                score = gradeLogData.score,
                time = gradeLogData.time,
                semester = gradeLogData.semesterString
                    .split("学年 - ").let { strings ->
                        val termIndex = when (strings[1]) {
                            "第一学期" -> 1
                            else -> 2
                        }
                        Semester.parse("${strings[0]}-$termIndex")
                    },
            )
        }

        return GradeLogs(
            userId = user!!.id,
            createAt = LocalDate.now(),
            logs = logs,
        )
    }

    /**
     * 成绩日志响应数据。
     *
     * @property actName 活动名称
     * @property proName 活动名称
     * @property optName 活动名称
     * @property category 分类名称
     * @property score 分数
     * @property timestamp 得分时间戳
     * @property semesterString 学期，2024-2025学年 - 第一学期
     */
    @Serializable
    private data class GradeLogData(
        val actName: String?,
        val proName: String?,
        val optName: String?,
        @SerialName("className") val category: String,
        @SerialName("score") val score: Double,
        @SerialName("sendTime") val timestamp: Long,
        @SerialName("xueqiName") val semesterString: String,
    ) {
        /** 活动名称 */
        val activityName = actName ?: proName ?: optName ?: ""

        /** 得分时间 */
        val time = LocalDateTime.from(timestamp)
    }

    override suspend fun getActivities() = getActivities(
        "/apps/activityImpl/list/getActivityByUser",
        null,
    )

    override suspend fun getMyActivities(
        state: BasicActivity.State,
    ) = getActivities(
        "/apps/activityImpl/getmyjoinactivitylist",
        state,
    )

    /**
     * 活动列表。
     */
    private suspend fun getActivities(
        urlString: String,
        state: BasicActivity.State?,
    ): List<BasicActivity> {
        val body = httpClient.get(urlString) {
            val map = mapOf("cur" to 1, "size" to 20)
            parameter(
                "para",
                Json.encodeToString(
                    if (state != null) {
                        map + mapOf("type" to state.value)
                    } else {
                        map
                    }
                )
            )
        }.body<Response<Records>>()

        return body.data.records.map {
            BasicActivity(
                id = it.id,
                name = it.name,
                category = it.category,
                score = it.score,
                duration = it.duration,
                organization = it.organization,
                logoUrl = it.logoUrl,
                type = it.type,
                isOnline = it.isOnline,
            )
        }
    }

    @Serializable
    private data class Records(val records: List<BasicActivityData>)

    /**
     * 活动响应数据。
     *
     * @property id 唯一标识
     * @property name 活动名称
     * @property category 分类
     * @property score 分数
     * @property startTimestamp 开始时间戳
     * @property endTimestamp 结束时间戳
     * @property organization 组织
     * @property logoUrl Logo Url
     * @property type 类型
     * @property oto 1 为线上，0 为线下
     */
    @Serializable
    private data class BasicActivityData(
        val id: Int,
        val name: String,
        val category: String,
        @SerialName("hours") val score: Double,
        @SerialName("startTime") val startTimestamp: Long,
        @SerialName("endTime") val endTimestamp: Long,
        @SerialName("orgName") val organization: String,
        @SerialName("logo") val logoUrl: String,
        @SerialName("typeName") val type: String,
        val oto: Int,
    ) {
        /** 持续时间 */
        val duration = LocalDateTime.from(startTimestamp)..
                LocalDateTime.from(endTimestamp)

        /** 是否为线上 */
        val isOnline = oto == 1
    }

    override suspend fun getActivity(id: Int): Activity {
        val body = httpClient.get("/apps/activityImpl/detail") {
            parameter(
                "para",
                Json.encodeToString(
                    mapOf("activityId" to id),
                ),
            )
        }.body<Response<ActivityData>>()
        val data = body.data

        return Activity(
            name = data.name,
            description = data.description,
            category = data.category,
            score = data.score,
            area = data.area,
            deadline = data.deadline,
            cover = data.cover,
            host = data.host,
            admin = data.admin,
            phoneNumber = data.phoneNumber,
            teacher = data.teacher,
            trainingHours = data.trainingHours,
            duration = data.duration,
            needSubmit = data.needSubmit,
            total = data.total,
            leftover = data.leftover,
            type = data.type,
        )
    }

    /**
     * 活动详情响应数据。
     */
    @Serializable
    private data class ActivityData(
        val name: String,
        @SerialName("introduce") val description: String,
        @SerialName("className") val category: String,
        @SerialName("hours") val score: Double,
        @SerialName("pitchAddress") val area: String,
        @SerialName("senrollEndTime") val deadlineString: String,
        @SerialName("haibaoUrl") val cover: String,
        @SerialName("zhubanName") val host: String,
        @SerialName("adminName") val admin: String,
        @SerialName("adminContact") val phoneNumber: String,
        @SerialName("teacherName") val teacher: String?,
        @SerialName("classHours") val trainingHours: Double,
        @SerialName("startTime") val startTimeString: String,
        @SerialName("endTime") val endTimeString: String,
        val subJob: Int,
        @SerialName("peopleLimit") val total: Int,
        @SerialName("peopleCount") val leftover: Int,
        @SerialName("typeName") val type: String,
    ) {
        /** 截止时间 */
        val deadline = LocalDateTime.parseIsoVariantWithoutSecond(deadlineString)
        /** 持续时间 */
        val duration = LocalDateTime.parseIsoVariant(startTimeString)..
                LocalDateTime.parseIsoVariant(endTimeString)
        /** 是否必须提交作业 */
        val needSubmit = subJob == 1
    }

    /**
     * 第二课堂用户。
     *
     * @property id 学号
     * @property systemId 第二课堂 id
     * @property token 访问令牌
     */
    private data class SecondClassUser(
        val id: String,
        val systemId: String? = null,
        val token: String? = null,
    )

    /**
     * 第二课堂接口响应内容。
     *
     * @property code 状态码
     * @property message 消息
     * @property data 数据
     */
    @Serializable
    private data class Response<T>(
        val code: Int,
        @SerialName("msg") val message: String,
        val data: T,
    )

    companion object {
        const val BASE_URL = "http://2ketang.gdufe.edu.cn"
        private const val LOGIN_URL = "/apps/common/login"
    }
}
