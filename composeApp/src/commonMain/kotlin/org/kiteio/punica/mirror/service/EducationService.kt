package org.kiteio.punica.mirror.service

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.select.Evaluator
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.plugin
import io.ktor.client.plugins.timeout
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
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.char
import org.kiteio.punica.mirror.modal.User
import org.kiteio.punica.mirror.modal.education.Campus
import org.kiteio.punica.mirror.modal.education.Course
import org.kiteio.punica.mirror.modal.education.CourseTable
import org.kiteio.punica.mirror.modal.education.Exam
import org.kiteio.punica.mirror.modal.education.Exams
import org.kiteio.punica.mirror.modal.education.Plan
import org.kiteio.punica.mirror.modal.education.Plans
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
    suspend fun getCaptcha(): ByteArray

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
    suspend fun getTimetable(semester: Semester): Timetable

    /**
     * 课程课表。
     */
    suspend fun getCourseTable(semester: Semester): CourseTable

    /**
     * 考试安排。
     */
    suspend fun getExams(): Exams

    /**
     * 执行计划。
     */
    suspend fun getPlans(): Plans

    /**
     * 学业进度。
     */
    suspend fun getProgress()

    /**
     * 教师。
     */
    suspend fun getTeacher()

    /**
     * 教师信息。
     */
    suspend fun getTeacherProfile()

    /**
     * 校历。
     */
    suspend fun getCalendar()

    /**
     * 学籍预警。
     */
    suspend fun getAlert()

    /**
     * 课程成绩。
     */
    suspend fun getCourseGrade()

    /**
     * 等级考试成绩。
     */
    suspend fun getLevelGrade()

    /**
     * 毕业审核。
     */
    suspend fun getGraduationAudit()

    /**
     * 培养方案。
     */
    suspend fun getProgramme()

    /**
     * 免听。
     */
    suspend fun getExemption()
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
    }

    override suspend fun getCaptcha() = httpClient
        .get("/jsxsd/verifycode.servlet")
        .readRawBytes()

    override suspend fun login(user: User, captcha: String) {
        this.user = user
        val text = httpClient.submitForm(
            "/jsxsd/xk/LoginToXkLdap",
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
        httpClient.get("/jsxsd/xk/LoginToXk") {
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
    override suspend fun getTimetable(semester: Semester): Timetable {
        return withContext(Dispatchers.Default) {
            val text = httpClient.get("/jsxsd/xskb/xskb_list.do") {
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
                note = note,
            )
        }
    }

    override suspend fun getCourseTable(semester: Semester): CourseTable {
        return withContext(Dispatchers.Default) {
            val text = httpClient.submitForm(
                "jsxsd/kbcx/kbxx_kc_ifr",
                parameters {
                    // 学期，yyyy-yyyy-T
                    append("xnxqh", "$semester")
                }
            ) {
                // 课程课表数据量非常大，需要额外设置更长的超时时间
                timeout { requestTimeoutMillis = 25000 }
            }.bodyAsText()

            val doc = Ksoup.parse(text)
            // #kbtable > tbody
            val tbody = doc.getElementById("kbtable")!!.firstElementChild()!!
            // #kbtable > tbody > tr
            val trs = tbody.children()

            // 用于匹配教师和上课周次
            val teacherWeeksRegex = Regex("^(.+?)?\\s*\\((.*?)\\)$")

            // 每一项为一行，包含同一个课程名的不同上课信息
            val coursesList = mutableListOf<List<Course>>()

            // 每一行包含一个课程的所有上课信息。从 2 开始，排除星期和节次表头
            for (index in 2..<trs.size) {
                val tds = trs[index].children()
                // 课程名称
                val name = tds[0].text()

                val courses = mutableListOf<Course>()

                // 排除课程名称，遍历一行课程的每一列
                for (index in 1..<tds.size) {
                    // #kbtable > tbody > tr > td > nobr > div.kbcontent1
                    val divs = tds[index]
                        .getElementsByClass("kbcontent1")

                    // 一格中包含多个课程
                    for (div in divs) {
                        val textNode = div.textNodes()
                        val (teacher, weeks) = teacherWeeksRegex
                            .find(textNode[1].text())!!
                            .destructured

                        val firstSection = index % 6 * 2 - 1
                        courses.add(
                            Course(
                                name = name,
                                teacher = teacher,
                                weeks = weeks,
                                classroom = textNode[2].text().trim(),
                                sections = setOf(firstSection, firstSection + 1),
                                dayOfWeek = DayOfWeek((index - 1) / 6 + 1),
                                clazz = textNode[0].text(),
                            )
                        )
                    }
                }
                if (courses.isNotEmpty()) coursesList.add(courses)
            }

            return@withContext CourseTable(
                semester = semester,
                createAt = LocalDate.now(),
                courses = coursesList,
            )
        }
    }

    override suspend fun getExams(): Exams {
        return withContext(Dispatchers.Default) {
            val text = httpClient.get("jsxsd/xsks/xsksap_list")
                .bodyAsText()

            val doc = Ksoup.parse(text)

            // # dataList > tbody
            val tbody = doc.getElementById("dataList")!!
                .firstElementChild()!!
            // #dataList > tbody > tr
            val trs = tbody.children()

            // yyyy-MM-dd HH:mm
            val formatter = LocalDateTime.Format {
                year(); char('-')
                monthNumber(); char('-')
                dayOfMonth(); char(' ')
                hour(); char(':')
                minute()
            }

            val exams = mutableListOf<Exam>()

            // 排除表头，每一行为一个课程的考试信息
            for (index in 1..<trs.size) {
                // #dataList > tbody > tr > td
                val tds = trs[index].children()
                // 考试时间
                val time = tds[index + 3].text().split("~")
                val startTime = LocalDateTime.parse(time[0], formatter)
                val endTime = LocalDateTime.parse(time[1], formatter)
                // 校区
                val campus = if (tds[index + 4].text() == "广州校区")
                    Campus.Canton else Campus.Foshan

                exams.add(
                    Exam(
                        courseId = tds[index + 1].text(),
                        courseName = tds[index + 2].text(),
                        duration = startTime..endTime,
                        campus = campus,
                        classroom = tds[index + 5].text(),
                    )
                )
            }

            return@withContext Exams(
                userId = user!!.id,
                createAt = LocalDate.now(),
                exams = exams,
            )
        }
    }

    override suspend fun getPlans(): Plans {
        return withContext(Dispatchers.Default) {
            val text = httpClient.get("jsxsd/pyfa/pyfa_query")
                .bodyAsText()

            val doc = Ksoup.parse(text)

            // # dataList > tbody
            val tbody = doc.getElementById("dataList")!!
                .firstElementChild()!!
            // #dataList > tbody > tr
            val trs = tbody.children()

            val plans = mutableListOf<Plan>()

            // 排除表头，每一行为一个课程的课程
            for (index in 1..<trs.size) {
                val tds = trs[index].children()
                plans.add(
                    Plan(
                        semester = Semester.parse(tds[1].text()),
                        courseId = tds[2].text(),
                        courseName = tds[3].text(),
                        department = tds[4].text(),
                        credits = tds[5].text().toDouble(),
                        hours = tds[6].text().toInt(),
                        assessment = tds[7].text(),
                        property = tds[8].text(),
                    )
                )
            }

            return@withContext Plans(
                userId = user!!.id,
                createAt = LocalDate.now(),
                plans = plans,
            )
        }
    }

    override suspend fun getProgress() {
        TODO("Not yet implemented")
    }

    override suspend fun getTeacher() {
        TODO("Not yet implemented")
    }

    override suspend fun getTeacherProfile() {
        TODO("Not yet implemented")
    }

    override suspend fun getCalendar() {
        TODO("Not yet implemented")
    }

    override suspend fun getAlert() {
        TODO("Not yet implemented")
    }

    override suspend fun getCourseGrade() {
        TODO("Not yet implemented")
    }

    override suspend fun getLevelGrade() {
        TODO("Not yet implemented")
    }

    override suspend fun getGraduationAudit() {
        TODO("Not yet implemented")
    }

    override suspend fun getProgramme() {
        TODO("Not yet implemented")
    }

    override suspend fun getExemption() {
        TODO("Not yet implemented")
    }
}