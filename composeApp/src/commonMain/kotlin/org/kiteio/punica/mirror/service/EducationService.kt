package org.kiteio.punica.mirror.service

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.select.Evaluator
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.plugin
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readRawBytes
import io.ktor.http.HttpHeaders
import io.ktor.http.parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.kiteio.punica.mirror.modal.User
import org.kiteio.punica.mirror.modal.education.Course
import org.kiteio.punica.mirror.modal.education.Semester
import org.kiteio.punica.mirror.modal.education.Timetable
import org.kiteio.punica.mirror.util.now

fun EducationService(): EducationService {
    val httpClient = HttpClient {
        defaultRequest {
            url(urlString = EducationServiceImpl.BASE_URL)
            header(HttpHeaders.AcceptEncoding, "br")
        }
        // TODO: 改用 intercept 存取 Cookie 并保存本地
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
    }

    return EducationServiceImpl(httpClient)
}

/**
 * 教务系统服务。
 */
interface EducationService {
    /**
     * 登录验证码。
     */
    suspend fun captcha(): ByteArray

    /**
     * 登录。
     * @param user 用户
     * @param captcha 验证码
     */
    suspend fun login(user: User, captcha: String)

    /**
     * 登出。
     */
    suspend fun logout()

    /**
     * 课表。
     */
    suspend fun timetable(semester: Semester): Timetable

    /**
     * 课程课表。
     */
    suspend fun courseTable()

    /**
     * 考试安排。
     */
    suspend fun exam()

    /**
     * 成绩。
     */
    suspend fun grade()

    /**
     * 执行计划。
     */
    suspend fun plan()

    /**
     * 学业进度。
     */
    suspend fun progress()

    /**
     * 教师。
     */
    suspend fun teacher()

    /**
     * 教师信息。
     */
    suspend fun teacherProfile()

    /**
     * 校历。
     */
    suspend fun calendar()

    /**
     * 学籍预警。
     */
    suspend fun alert()

    /**
     * 课程成绩。
     */
    suspend fun courseGrade()

    /**
     * 等级考试成绩。
     */
    suspend fun levelGrade()

    /**
     * 毕业审核。
     */
    suspend fun graduationAudit()

    /**
     * 培养方案。
     */
    suspend fun programme()

    /**
     * 免听。
     */
    suspend fun exemption()
}

// --------------- 实现 ---------------

private class EducationServiceImpl(private val httpClient: HttpClient) : EducationService {
    private var user: User? = null

    init {
        httpClient.plugin(HttpSend).intercept {
            execute(it)
        }
    }

    companion object {
        const val BASE_URL = "http://jwxt.gdufe.edu.cn"
        const val CAPTCHA_URL = "/jsxsd/verifycode.servlet"
        const val LOGIN_URL = "/jsxsd/xk/LoginToXkLdap"
        const val LOGOUT_URL = "/jsxsd/xk/LoginToXk"
        const val TIMETABLE_URL = "/jsxsd/xskb/xskb_list.do"
    }

    override suspend fun captcha() = httpClient
        .get(CAPTCHA_URL)
        .readRawBytes()

    override suspend fun login(user: User, captcha: String) {
        this.user = user
        val text = httpClient.submitForm(
            LOGIN_URL,
            parameters {
                append("USERNAME", user.id)
                append("PASSWORD", user.password)
                append("RANDOMCODE", captcha)
            }
        ).bodyAsText()

        // 返回空字符串则登录成功
        check(text.isEmpty()) {
            this.user = null
            // 登录失败，获取登录页的 font 错误信息抛出异常；
            // 若错误信息为空，则获取页面 title 抛出
            withContext(Dispatchers.Default) {
                val doc = Ksoup.parse(text)
                val message = doc.selectFirst(Evaluator.Tag("font"))?.text()

                message ?: doc.title()
            }
        }
    }

    override suspend fun logout() {
        httpClient.get(LOGOUT_URL) {
            parameter("method", "exit")
            parameter("tktime", Clock.System.now().toEpochMilliseconds())
        }.also { println(it.bodyAsText()) }
    }

    /**
     * 课程 html 片段：
     * <div id="1DC90495E58E4A07BA62065B715AF401-1-2" style="" class="kbcontent">
     *   课程名称
     *   <br>
     *   <font title="老师">教师名</font>
     *   <br>
     *   <font title="周次(节次)">周次</font>
     *   <br>
     *   <font title="教室">上课地点</font>
     *   <br>
     *   [01-02]节
     *   <br>
     *   ---------------------
     *   <br>
     *   课程名称
     *   <br>
     *   <font title="老师">教师名</font>
     *   <br>
     *   <font title="周次(节次)">周次</font>
     *   <br>
     *   <font title="教室">上课地点</font>
     *   <br>[01-02]节
     *   <br>
     * </div>
     */
    override suspend fun timetable(semester: Semester): Timetable {
        return withContext(Dispatchers.Default) {
            val text = httpClient.get(TIMETABLE_URL) {
                // 学期，yyyy-yyyy-T
                parameter("xnxq01id", semester)
            }.bodyAsText()

            val doc = Ksoup.parse(text)
            val courseDividerRegex = Regex("-{21}")
            val numberRegex = Regex("\\d+")

            // #kbtable > tbody
            val tbody = doc.getElementById("kbtable")!!.firstElementChild()!!

            // #kbtable > tbody > tr > td > div.kbcontent
            val divs = tbody.getElementsByClass("kbcontent")

            // 每一项为一格，包含多个 Course
            val coursesList = mutableListOf<List<Course>?>()
            // 从左到右，总共 7 天
            for (columnIndex in 0..6) {
                val dayOfWeek = DayOfWeek(columnIndex + 1)
                // 从上到下，总共 6 大节
                for (rowIndex in 0..5) {
                    val div = divs[rowIndex * 7 + columnIndex]

                    // 没有课程
                    if (div.childrenSize() == 0) {
                        coursesList.add(null)
                        continue
                    }

                    // 课程名称 + 节次 + 分割线
                    val textNodes = div.textNodes()
                    // 教师 + 周次 + 教室
                    val segments = div.html().split(courseDividerRegex)

                    val courses = mutableListOf<Course>()
                    val firstSection = rowIndex * 2 + 1

                    for (index in segments.indices) {
                        val mappedIndex = index * 3
                        // 节次，通过数字正则提取
                        val sections = numberRegex
                            .findAll(textNodes[mappedIndex + 1].text())
                            .map { it.value.toInt() }
                            .toSet()

                        // 跳过连上课程的后两节，[01-02-03-04] 节会有重复的两节
                        if (sections.count { it < firstSection } == 2) continue

                        val builder = Course.Builder()

                        // 课程名称
                        builder.name = textNodes[mappedIndex].text()
                        // 星期
                        builder.dayOfWeek = dayOfWeek
                        // 节次
                        builder.sections = sections

                        // #kbtable > tbody > tr > td > div.kbcontent > font
                        val fonts = Ksoup.parse(segments[index])
                            .getElementsByTag("font")

                        for (font in fonts) {
                            val fontText = font.text()

                            when (font.attr("title")) {
                                "老师" -> builder.teacher = fontText
                                "周次(节次)" -> builder.weeks = fontText
                                "教室" -> builder.classroom = fontText
                            }
                        }

                        // 整理一格
                        courses.add(builder.build())
                    }

                    // 整理整个课表，添加 ifEmpty 是因为即使有课程也会被跳过
                    coursesList.add(courses.ifEmpty { null })
                }
            }

            // 备注
            val note = runCatching {
                // #kbtable > tbody > tr:nth-child(8) > td
                tbody.select("tr:nth-child(8) > td")
                    .text()
                    .takeIf { it != "未安排时间课程：" }
            }.getOrNull()

            return@withContext Timetable(
                userId = user!!.id,
                createAt = LocalDate.now(),
                semester = semester,
                courses = coursesList,
                note = note
            )
        }
    }

    override suspend fun courseTable() {
        TODO("Not yet implemented")
    }

    override suspend fun exam() {
        TODO("Not yet implemented")
    }

    override suspend fun grade() {
        TODO("Not yet implemented")
    }

    override suspend fun plan() {
        TODO("Not yet implemented")
    }

    override suspend fun progress() {
        TODO("Not yet implemented")
    }

    override suspend fun teacher() {
        TODO("Not yet implemented")
    }

    override suspend fun teacherProfile() {
        TODO("Not yet implemented")
    }

    override suspend fun calendar() {
        TODO("Not yet implemented")
    }

    override suspend fun alert() {
        TODO("Not yet implemented")
    }

    override suspend fun courseGrade() {
        TODO("Not yet implemented")
    }

    override suspend fun levelGrade() {
        TODO("Not yet implemented")
    }

    override suspend fun graduationAudit() {
        TODO("Not yet implemented")
    }

    override suspend fun programme() {
        TODO("Not yet implemented")
    }

    override suspend fun exemption() {
        TODO("Not yet implemented")
    }
}