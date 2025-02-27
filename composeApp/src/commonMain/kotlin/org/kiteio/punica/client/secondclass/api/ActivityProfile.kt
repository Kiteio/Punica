package org.kiteio.punica.client.secondclass.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.secondclass.SecondClass
import org.kiteio.punica.client.secondclass.foundation.SecondClassBody
import org.kiteio.punica.serialization.Json

/**
 * 返回 [activityId] 活动详情。
 */
suspend fun SecondClass.getActivityProfile(activityId: Int): ActivityProfile {
    return withContext(Dispatchers.Default) {
        val body = get("apps/activityImpl/detail") {
            parameter(
                "para",
                Json.encodeToString(mapOf("activityId" to activityId)),
            )
            header("X-Token", token)
        }.body<ActivityProfileBody>()

        require(body.code == 200) { body.msg }

        return@withContext body.data
    }
}


/**
 * 活动详情。
 *
 * @property name 活动名称
 * @property description 描述
 * @property category 分类
 * @property score 分数
 * @property area 地点
 * @property deadline 报名截止时间
 * @property cover 封面
 * @property host 主办方
 * @property admin 管理员
 * @property phoneNumber 手机号
 * @property teacher 指导老师
 * @property trainingHours 培训时间
 * @property duration 持续时间
 * @property needSubmit 是否必须提交作业
 * @property total 最大人数
 * @property leftover 当前人数
 * @property type 类型
 */
interface ActivityProfile {
    val name: String
    val description: String
    val category: String
    val score: Double
    val area: String
    val deadline: String
    val cover: String
    val host: String
    val admin: String
    val phoneNumber: String
    val teacher: String?
    val trainingHours: Double
    val duration: ClosedRange<String>
    val needSubmit: Boolean
    val total: Int
    val leftover: Int
    val type: String
}


/**
 * 活动详情响应内容。
 */
@Serializable
data class ActivityProfileBody(
    override val code: Int,
    override val msg: String,
    override val data: ActivityProfileImpl,
) : SecondClassBody<ActivityProfileImpl>


/**
 * 活动详情。
 */
@Serializable
data class ActivityProfileImpl(
    override val name: String,
    @SerialName("introduce") override val description: String,
    @SerialName("className") override val category: String,
    @SerialName("hours") override val score: Double,
    @SerialName("pitchAddress") override val area: String,
    @SerialName("senrollEndTime") override val deadline: String,
    @SerialName("haibaoUrl") override val cover: String,
    @SerialName("zhubanName") override val host: String,
    @SerialName("adminName") override val admin: String,
    @SerialName("adminContact") override val phoneNumber: String,
    @SerialName("teacherName") override val teacher: String?,
    @SerialName("classHours") override val trainingHours: Double,
    val startTime: String,
    val endTime: String,
    val subJob: Int,
    @SerialName("peopleLimit") override val total: Int,
    @SerialName("peopleCount") override val leftover: Int,
    @SerialName("typeName") override val type: String,
) : ActivityProfile {
    override val duration = startTime..endTime
    override val needSubmit = subJob == 1
}