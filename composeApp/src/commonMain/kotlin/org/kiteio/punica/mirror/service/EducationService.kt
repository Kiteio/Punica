package org.kiteio.punica.mirror.service

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
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
import org.kiteio.punica.mirror.modal.education.Alert
import org.kiteio.punica.mirror.modal.education.Alerts
import org.kiteio.punica.mirror.modal.education.BasicTeacher
import org.kiteio.punica.mirror.modal.education.Campus
import org.kiteio.punica.mirror.modal.education.Course
import org.kiteio.punica.mirror.modal.education.CourseGrade
import org.kiteio.punica.mirror.modal.education.CourseGrades
import org.kiteio.punica.mirror.modal.education.CourseTable
import org.kiteio.punica.mirror.modal.education.Exam
import org.kiteio.punica.mirror.modal.education.Exams
import org.kiteio.punica.mirror.modal.education.Exemption
import org.kiteio.punica.mirror.modal.education.Exemptions
import org.kiteio.punica.mirror.modal.education.GraduationAudit
import org.kiteio.punica.mirror.modal.education.LevelGrade
import org.kiteio.punica.mirror.modal.education.LevelGrades
import org.kiteio.punica.mirror.modal.education.Plan
import org.kiteio.punica.mirror.modal.education.Plans
import org.kiteio.punica.mirror.modal.education.Progress
import org.kiteio.punica.mirror.modal.education.ProgressModule
import org.kiteio.punica.mirror.modal.education.Progresses
import org.kiteio.punica.mirror.modal.education.Semester
import org.kiteio.punica.mirror.modal.education.Teacher
import org.kiteio.punica.mirror.modal.education.Teachers
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
     *
     * @param semester 学期
     */
    suspend fun getTimetable(semester: Semester): Timetable

    /**
     * 课程课表。
     *
     * @param semester 学期
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
    suspend fun getProgresses(): Progresses

    /**
     * 教师列表。
     *
     * @param query 查询
     * @param page 页码
     */
    suspend fun getTeachers(query: String, page: Int = 1): Teachers

    /**
     * 教师信息。
     *
     * @param id 工号
     */
    suspend fun getTeacher(id: String): Teacher

    /**
     * 学期日期。
     *
     * @param semester 学期
     */
    suspend fun getCalendar(semester: Semester): ClosedRange<LocalDate>

    /**
     * 学籍预警。
     *
     * @param page 页码
     */
    suspend fun getAlerts(page: Int = 1): Alerts

    /**
     * 课程成绩。
     */
    suspend fun getCourseGrades(): CourseGrades

    /**
     * 等级考试成绩。
     */
    suspend fun getLevelGrades(): LevelGrades

    /**
     * 毕业审核。
     */
    suspend fun getGraduationAudit(isPrimary: Boolean = true): GraduationAudit

    /**
     * 毕业审核报告。
     */
    suspend fun getGraduationAuditReport(urlString: String): ByteArray

    /**
     * 培养方案。
     */
    suspend fun getProgramme(): String

    /**
     * 免听。
     */
    suspend fun getExemptions(semester: Semester): Exemptions
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
        }
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
                "/jsxsd/kbcx/kbxx_kc_ifr",
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
            val text = httpClient.get("/jsxsd/xsks/xsksap_list")
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
            val text = httpClient.get("/jsxsd/pyfa/pyfa_query")
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
                // #dataList > tbody > tr > td
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

    override suspend fun getProgresses(): Progresses {
        return withContext(Dispatchers.Default) {
            val text = httpClient.submitForm(
                "/jsxsd/pyfa/xyjdcx",
                parameters {
                    // 主修（0）或辅修（1）
                    append("xdlx", "0")
                },
            ) {
                parameter("type", "cx")
            }.bodyAsText()

            val doc = Ksoup.parse(text)
            // body > div.Nsb_pw > div.Nsb_layout_r > table.Nsb_r_list.Nsb_table
            val tables = doc.getElementsByClass("Nsb_r_list Nsb_table")

            val modules = mutableListOf<ProgressModule>()
            for (table in tables) {
                // body > div.Nsb_pw > div.Nsb_layout_r >
                // table.Nsb_r_list.Nsb_table > tbody > tr
                val trs = table.firstElementChild()!!.children()

                val progresses = mutableListOf<Progress>()

                // 排除模块名称和表头
                for (index in 2..<trs.size - 1) {
                    // body > div.Nsb_pw > div.Nsb_layout_r >
                    // table.Nsb_r_list.Nsb_table > tbody > tr > td
                    val tds = trs[index].children()
                    progresses.add(
                        Progress(
                            name = tds[0].text(),
                            property = tds[1].text(),
                            courseId = tds[2].text(),
                            courseName = tds[3].text(),
                            credits = tds[4].text().toDouble(),
                            termIndex = tds[5].text().toIntOrNull(),
                            privilege = tds[6].text().ifBlank { null },
                            requiredCredits = tds[7].text().toDoubleOrNull(),
                            earnedCredits = tds[8].text().toDoubleOrNull(),
                        )
                    )
                }

                // 模块名称
                val name = trs[0].firstElementChild()!!.textNodes()[0].text().trim()
                // 倒数一个 tr 为学分汇总
                val lastTrChildren = trs.last()?.children()!!
                val lastTd = lastTrChildren[lastTrChildren.lastIndex]
                val beforeLastTd = lastTrChildren[lastTrChildren.lastIndex - 1]

                modules.add(
                    ProgressModule(
                        name = name,
                        requiredCredits = beforeLastTd.text().toDoubleOrNull(),
                        earnedCredits = lastTd.text().toDoubleOrNull(),
                        progresses = progresses,
                    )
                )
            }

            return@withContext Progresses(
                userId = user!!.id,
                createAt = LocalDate.now(),
                modules = modules
            )
        }
    }

    override suspend fun getTeachers(query: String, page: Int): Teachers {
        return withContext(Dispatchers.Default) {
            val text = httpClient.submitForm(
                "/jsxsd/jsxx/jsxx_list",
                parameters {
                    // 姓名
                    append("jsxm", query)
                    // 页码
                    append("pageIndex", "$page")
                }
            ).bodyAsText()

            val doc = Ksoup.parse(text)

            // #Form1
            val form = doc.getElementById("Form1")!!
            // #Form1 > table.Nsb_r_list.Nsb_table > tbody
            val tbody = form.selectFirst(
                Evaluator.Class("Nsb_r_list Nsb_table"),
            )!!.child(2)
            // #Form1 > table.Nsb_r_list.Nsb_table > tbody > tr
            val trs = tbody.children()

            val teachers = mutableListOf<BasicTeacher>()
            // 排除表头，每一行为一位教师
            for (index in 1..<trs.size) {
                // 查询结果为空
                if (trs[index].childrenSize() == 1) break

                // #Form1 > table.Nsb_r_list.Nsb_table > tbody > tr > td
                val tds = trs[index].children()

                teachers.add(
                    BasicTeacher(
                        id = tds[1].text(),
                        name = tds[2].text(),
                        factorial = tds[3].text(),
                    )
                )
            }

            return@withContext Teachers(
                pageCount = form.getPageCount(),
                teachers = teachers
            )
        }
    }

    /**
     * 返回 [Element] 中匹配 #PagingControl1_divOuterClass >
     * div > div.Nsb_r_list_fy3 子项的页数。
     */
    private fun Element.getPageCount(): Int {
        // #PagingControl1_divOuterClass > div > div.Nsb_r_list_fy3
        val div = selectFirst(
            Evaluator.Class("Nsb_r_list_fy3")
        )!!

        return Regex("\\d+").find(div.text())!!
            .value.toInt()
    }

    override suspend fun getTeacher(id: String): Teacher {
        return withContext(Dispatchers.Default) {
            val text = httpClient.get("jsxsd/jsxx/jsxx_query_detail") {
                // 教师工号
                parameter("jg0101id", id)
            }.bodyAsText()

            val doc = Ksoup.parse(text)

            // body > div.Nsb_pw > div.Nsb_layout_r >
            // form > table.no_border_table > tbody
            val tbody = doc.selectFirst(
                Evaluator.Class("no_border_table")
            )!!.child(0)
            // body > div.Nsb_pw > div.Nsb_layout_r >
            // form > table.no_border_table > tbody > tr
            val trs = tbody.children()

            val builder = Teacher.Builder().apply {
                this.id = id
            }
            var hasContact = false

            builder.name = trs[1].child(2).text()
            builder.gender = trs[1].child(4).text()
                .takeIf { it != "未说明的性别" }

            builder.politics = trs[2].child(1).text()
                .ifEmpty { null }
            builder.nation = trs[2].child(3).text()
                .ifEmpty { null }

            builder.duty = trs[3].child(1).text()
                .ifEmpty { null }
            builder.title = trs[3].child(3).text()
                .ifEmpty { null }

            builder.category = trs[4].child(1).text()
                .ifEmpty { null }
            builder.factorial = trs[4].child(3).text()
                .ifEmpty { null }

            builder.office = trs[5].child(1).text()
                .takeIf { it != "无" }
            builder.qualification = trs[5].child(3).text()
                .ifEmpty { null }

            builder.degree = trs[6].child(2).text()
                .ifEmpty { null }
            builder.field = trs[6].child(4).text()
                .ifEmpty { null }

            if (trs[7].child(1).text() != "联系方式：") {
                hasContact = true
                builder.phoneNumber = trs[7].child(2).text()
                    .ifEmpty { null }
                builder.qq = trs[7].child(4).text()
                    .ifEmpty { null }
            }

            if (hasContact) {
                builder.weChat = trs[8].child(2).text()
                    .ifEmpty { null }
                builder.email = trs[8].child(4).text()
                    .ifEmpty { null }
            }

            // 个人简介
            builder.biography = trs[trs.lastIndex - 8].text()
                .takeIf { it != "暂无数据" }

            // 近四个学期主讲课程
            trs.parseSimpleCourses(trs.lastIndex - 6, builder.taught::add)
            // 下学期计划开设课程
            trs.parseSimpleCourses(trs.lastIndex - 4, builder.teaching::add)

            // 教育理念
            builder.philosophy = trs[trs.lastIndex - 2].text().takeIf { it != "暂无数据" }

            // 最想对学生说的话
            builder.slogan = trs[trs.lastIndex].text().takeIf { it != "暂无数据" }

            return@withContext builder.build()
        }
    }

    private inline fun Elements.parseSimpleCourses(index: Int, action: (Teacher.Course) -> Unit) {
        val tds = get(index).selectFirst(
            Evaluator.Tag("tbody")
        )!!.getElementsByTag("td")

        if (tds.size != 1) {
            for (tdIndex in tds.indices step 4) {
                action(
                    Teacher.Course(
                        name = tds[tdIndex + 1].text(),
                        category = tds[tdIndex + 2].text(),
                        semester = Semester.parse(tds[tdIndex + 3].text()),
                    )
                )
            }
        }
    }

    override suspend fun getCalendar(semester: Semester): ClosedRange<LocalDate> {
        return withContext(Dispatchers.Default) {
            val text = httpClient.submitForm(
                "jsxsd/jxzl/jxzl_query",
                parameters {
                    // 学期，yyyy-yyyy-T
                    append("xnxq01id", "$semester")
                }
            ).bodyAsText()

            val doc = Ksoup.parse(text)

            // #kbtable > tbody
            val tbody = doc.getElementById("kbtable")!!
                .firstElementChild()!!

            // yyyy年MM月DD
            val formatter = LocalDate.Format {
                year(); char('年')
                monthNumber();char('月')
                dayOfMonth()
            }

            // 这一行中包含学期开始时间
            val firstTr = tbody.child(1)
            // 这一行中包含学期结束时间
            val lastTr = tbody.child(tbody.childrenSize() - 2)

            val startTdTitle = firstTr.children().first {
                it.hasAttr("title")
            }.attr("title")
            val endTdTitle = lastTr.children().last {
                it.hasAttr("title")
            }.attr("title")

            val start = formatter.parse(startTdTitle)
            val end = formatter.parse(endTdTitle)

            return@withContext start..end
        }
    }

    override suspend fun getAlerts(page: Int): Alerts {
        return withContext(Dispatchers.Default) {
            val text = httpClient.submitForm(
                "/jsxsd/xsxj/xsyjxx.do",
                parameters {
                    append("pageIndex", "$page")
                }
            ).bodyAsText()

            val doc = Ksoup.parse(text)

            // #Form1
            val form = doc.getElementById("Form1")!!
            // #Form1 > table.Nsb_r_list.Nsb_table > tbody
            val tbody = form.selectFirst(
                Evaluator.Class("Nsb_r_list Nsb_table"),
            )!!.child(0)
            // #Form1 > table.Nsb_r_list.Nsb_table > tbody > tr
            val trs = tbody.children()

            val alerts = mutableListOf<Alert>()

            // 排除表头，每一行为一个预警
            for (index in 1..<trs.size) {
                // #Form1 > table.Nsb_r_list.Nsb_table > tbody > tr > td
                val tds = trs[index].children()
                alerts.add(
                    Alert(
                        semester = Semester.parse(tds[1].text()),
                        importance = when (tds[3].text()) {
                            "红色" -> Alert.Importance.High
                            "黄色" -> Alert.Importance.Medium
                            "蓝色" -> Alert.Importance.Low
                            else -> Alert.Importance.Unknown
                        },
                        description = tds[6].text(),
                        value = tds[7].text(),
                    )
                )
            }

            return@withContext Alerts(
                userId = user!!.id,
                createAt = LocalDate.now(),
                alerts = alerts,
                pageCount = form.getPageCount(),
            )
        }
    }

    override suspend fun getCourseGrades(): CourseGrades {
        return withContext(Dispatchers.Default) {
            val text = httpClient.get("/jsxsd/kscj/cjcx_list")
                .bodyAsText()

            val doc = Ksoup.parse(text)

            // #dataList > tbody
            val tbody = doc.getElementById("dataList")!!
                .firstElementChild()!!
            // #dataList > tbody > tr
            val trs = tbody.children()

            val grades = mutableListOf<CourseGrade>()

            // 排除表头，每一行为一个课程
            for (index in 1..<trs.size) {
                // #dataList > tbody > tr > td
                val tds = trs[index].children()

                grades.add(
                    CourseGrade(
                        semester = Semester.parse(tds[1].text()),
                        courseId = tds[2].text(),
                        name = tds[3].text(),
                        usualScore = tds[4].text().ifEmpty { null },
                        labScore = tds[5].text().ifEmpty { null },
                        finalScore = tds[6].text().ifEmpty { null },
                        score = tds[7].text(),
                        credits = tds[8].text().toDouble(),
                        hours = tds[9].text().toInt(),
                        assessment = tds[10].text(),
                        property = tds[11].text(),
                        category = tds[12].text(),
                        electiveCategory = tds[13].text().ifEmpty { null },
                        examCategory = tds[14].text(),
                        mark = tds[15].text().ifEmpty { null },
                        note = tds[16].text().ifEmpty { null },
                    )
                )
            }

            // body > div.Nsb_pw
            val overview = doc.body().child(4).run {
                // body > div.Nsb_pw > div.Nsb_r_title
                child(1).remove()
                // #dataList
                lastElementChild()?.remove()
                text().replace("查询条件：全部 ", "")
            }

            return@withContext CourseGrades(
                userId = user!!.id,
                createAt = LocalDate.now(),
                overview = overview,
                grades = grades,
            )
        }
    }

    override suspend fun getLevelGrades(): LevelGrades {
        return withContext(Dispatchers.Default) {
            val text = httpClient.get("/jsxsd/kscj/djkscj_list")
                .bodyAsText()

            val doc = Ksoup.parse(text)

            // #dataList > tbody
            val tbody = doc.getElementById("dataList")!!
                .firstElementChild()!!
            // #dataList > tbody > tr
            val trs = tbody.children()

            val grades = mutableListOf<LevelGrade>()

            // 排除 2 个表头
            for (index in 2..<trs.size) {
                // #dataList > tbody > tr > td
                val tds = trs[index].children()
                grades.add(
                    LevelGrade(
                        name = tds[1].text(),
                        score = tds[4].text(),
                        date = LocalDate.parse(tds[8].text()),
                    )
                )
            }

            return@withContext LevelGrades(
                userId = user!!.id,
                createAt = LocalDate.now(),
                grades = grades,
            )
        }
    }

    override suspend fun getGraduationAudit(isPrimary: Boolean): GraduationAudit {
        return withContext(Dispatchers.Default) {
            var text = httpClient.get("/jsxsd/bygl/bybm")
                .bodyAsText()
            var doc = Ksoup.parse(text)
            // #bybm > option
            val option = doc.getElementById("bybm")!!
                .children()
                // 筛选出最新
                .apply { sortByDescending { it.text() } }
                .run { if (isPrimary) get(1) else get(0) }

            text = httpClient.submitForm(
                "/jsxsd/bygl/bybmcz.do",
                parameters {
                    append("bybm", option.value())
                },
            ).bodyAsText()
            doc = Ksoup.parse(text)

            // #Form1 > table.Nsb_r_list.Nsb_table > tbody
            val tbody = doc
                .getElementsByClass("Nsb_r_list Nsb_table")[1]
                .firstElementChild()!!
            // #Form1 > table.Nsb_r_list.Nsb_table > tbody > tr
            val trs = tbody.children().also {
                check(it.size == 2)
            }
            // #Form1 > table.Nsb_r_list.Nsb_table > tbody > tr > td
            val tds = trs[1].children()

            // javascript:PrintDoc('[reportUrl]')
            val href = tds[8].selectFirst(
                Evaluator.Tag("a"),
            )!!.attr("href")
            val reportUrl = Regex("\\('([^']*)'\\)")
                .find(href)!!
                .groupValues[1]

            return@withContext GraduationAudit(
                userId = user!!.id,
                createAt = LocalDate.now(),
                year = tds[0].text().toInt(),
                name = tds[1].text(),
                category = tds[2].text(),
                channel = tds[3].text(),
                credits = tds[4].text().toDouble(),
                completionRate = tds[5].text().toDouble(),
                enrolmentRate = tds[6].text().toDouble(),
                note = tds[7].text().ifEmpty { null },
                reportUrl = reportUrl,
            )
        }
    }

    override suspend fun getGraduationAuditReport(
        urlString: String,
    ): ByteArray {
        return httpClient.get(urlString) {
            timeout { requestTimeoutMillis = 2000 }
        }.readRawBytes()
    }

    override suspend fun getProgramme(): String {
        val text = httpClient.get("/jsxsd/pyfa/pyfazd_query")
            .bodyAsText()

        val doc = Ksoup.parse(text)
        return doc.selectFirst(Evaluator.Tag("form"))!!.html()
    }

    override suspend fun getExemptions(semester: Semester): Exemptions {
        return withContext(Dispatchers.Default) {
            val text = httpClient.submitForm(
                "/jsxsd/kscj/mtsq_list",
                parameters {
                    append("xnxqid", "${semester}")
                }
            ).bodyAsText()

            val doc = Ksoup.parse(text)

            // #Form1 > table.Nsb_r_list.Nsb_table > tbody
            val tbody = doc.selectFirst(
                Evaluator.Class("Nsb_r_list Nsb_table"),
            )!!.firstElementChild()!!
            // #Form1 > table.Nsb_r_list.Nsb_table > tbody > tr
            val trs = tbody.children()

            val exemptions = mutableListOf<Exemption>()
            // 排除表头，每一行为一个课程
            for (index in 1..<trs.size) {
                // #Form1 > table.Nsb_r_list.Nsb_table > tbody > tr > td
                val tds = trs[index].children()
                exemptions.add(
                    Exemption(
                        courseId = tds[2].text(),
                        courseName = tds[3].text(),
                        department = tds[4].text(),
                        teacher = tds[5].text(),
                        hours = tds[6].text().toInt(),
                        credits = tds[7].text().toDouble(),
                        assessment = tds[8].text(),
                        reason = tds[9].text().ifEmpty { null },
                        status = tds[10].text().ifEmpty { null },
                        time = tds[11].text().ifEmpty { null }
                            ?.let { LocalDate.parse(it) },
                    )
                )
            }

            return@withContext Exemptions(
                userId = user!!.id,
                createAt = LocalDate.now(),
                semester = semester,
                exemptions = exemptions,
            )
        }
    }
}