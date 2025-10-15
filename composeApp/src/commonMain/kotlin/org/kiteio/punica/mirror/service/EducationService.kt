package org.kiteio.punica.mirror.service

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import com.fleeksoft.ksoup.select.Evaluator
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.isoDayNumber
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.kiteio.punica.mirror.modal.education.*
import org.kiteio.punica.mirror.util.Json
import org.kiteio.punica.mirror.util.now
import org.kiteio.punica.mirror.util.parseIsoVariantWithoutSecond
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * 教务系统服务。
 */
fun EducationService(): EducationService {
    val cookiesStorage = AcceptAllCookiesStorage()
    val httpClient = HttpClient {
        defaultRequest {
            url(EducationServiceImpl.BASE_URL)
        }
        install(HttpCookies) {
            storage = cookiesStorage
        }
    }

    return EducationServiceImpl(httpClient, cookiesStorage)
}

/**
 * 教务系统服务。
 */
interface EducationService {
    /**
     * 登录验证码。
     */
    suspend fun getCaptcha(cookie: Cookie? = null): ByteArray

    /**
     * 登录。
     *
     * @param userId 学号
     * @param password 门户密码
     * @param captcha 验证码
     */
    suspend fun login(userId: String, password: String, captcha: String)

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
     *
     * @param isPrimary 是否主修
     */
    suspend fun getGraduationAudit(isPrimary: Boolean = true): GraduationAudit

    /**
     * 毕业审核报告。
     *
     * @param urlString 报告下载 URL
     */
    suspend fun getGraduationAuditReport(urlString: String): ByteArray

    /**
     * 培养方案。
     */
    suspend fun getProgramme(): String

    /**
     * 免听。
     *
     * @param semester 学期
     */
    suspend fun getExemptions(semester: Semester): Exemptions

    /**
     * 选课系统入口。
     *
     * @param token 选课系统 id，每一轮会有一个不同的 id
     */
    suspend fun getCourseSystem(token: String?): CourseSystem?

    /**
     * 可选课程。
     *
     * @param category 特殊类别
     * @param page 页码
     * @param size 页面内容量
     */
    suspend fun getCourses(
        category: SelectableCourse.Category.Special,
        page: Int = 1,
        size: Int = 15,
    ): List<SelectableCourse>

    /**
     * 可选课程。
     *
     * @param category 通用类别
     * @param page 页码
     * @param size 页面内容量
     */
    suspend fun getCourses(
        category: SelectableCourse.Category.Common,
        parameters: SelectableCourse.Parameters,
        page: Int = 1,
        size: Int = 15,
    ): List<SelectableCourse>

    /**
     * 选课。
     *
     * @param id 课程唯一标识
     * @param category 课程类别
     */
    suspend fun selectCourse(
        id: String,
        category: SelectableCourse.Category,
        priority: SelectableCourse.Priority?,
    )

    /**
     * 退课。
     *
     * @param id 课程唯一标识
     */
    suspend fun deleteCourse(id: String)

    /**
     * 选课学分总览。
     */
    suspend fun getCoursesOverview(): CoursesOverview

    /**
     * 已选课程。
     */
    suspend fun getSelectedCourses(): List<SelectedCourse>

    /**
     * 退课日志。
     */
    suspend fun getCourseLogs(): List<CourseLog>
}

// --------------- 实现 ---------------

private class EducationServiceImpl(
    private val httpClient: HttpClient,
    private val storage: CookiesStorage,
) : EducationService {
    private var user: EducationUser? = null

    init {
        httpClient.plugin(HttpSend).intercept {
            execute(it)
        }
    }

    companion object {
        const val BASE_URL = "http://jwxt.gdufe.edu.cn"
    }

    override suspend fun getCaptcha(cookie: Cookie?): ByteArray {
        cookie?.let {
            storage.addCookie(BASE_URL, it)
        }
        return httpClient.get("/jsxsd/verifycode.servlet")
            .readRawBytes()
    }

    override suspend fun login(userId: String, password: String, captcha: String) {
        user = EducationUser(userId, password)
        val text = httpClient.submitForm(
            "/jsxsd/xk/LoginToXkLdap",
            parameters {
                append("USERNAME", userId)
                append("PASSWORD", password)
                append("RANDOMCODE", captcha)
            }
        ).bodyAsText()

        // 返回空字符串则登录成功
        check(text.isEmpty()) {
            user = null
            // 登录失败，获取登录页的 font 错误信息抛出异常；
            // 若错误信息为空，则获取页面 title 抛出
            withContext(Dispatchers.Default) {
                val doc = Ksoup.parse(text)
                val message = doc.selectFirst(
                    Evaluator.Tag("font"),
                )?.text()

                message ?: doc.title()
            }
        }
    }

    /**
     * 教务系统用户。
     *
     * @property id 学号
     * @property password 门户密码
     */
    private data class EducationUser(val id: String, val password: String)

    @OptIn(ExperimentalTime::class)
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
    override suspend fun getTimetable(
        semester: Semester,
    ) = withContext(Dispatchers.Default) {
        val text = httpClient.get("/jsxsd/xskb/xskb_list.do") {
            // 学期，yyyy-yyyy-T
            parameter("xnxq01id", semester)
        }.bodyAsText()

        val doc = Ksoup.parse(text)
        val courseDividerRegex = Regex("-{21}")

        // #kbtable > tbody
        val tbody = doc.getElementById("kbtable")!!
            .firstElementChild()!!

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
                    val sections = textNodes[mappedIndex + 1].text()
                        .parseAsSections()

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

    override suspend fun getCourseTable(
        semester: Semester,
    ) = withContext(Dispatchers.Default) {
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
        val tbody = doc.getElementById("kbtable")!!
            .firstElementChild()!!
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

    override suspend fun getExams() = withContext(
        Dispatchers.Default,
    ) {
        val text = httpClient.get("/jsxsd/xsks/xsksap_list")
            .bodyAsText()

        val doc = Ksoup.parse(text)

        // # dataList > tbody
        val tbody = doc.getElementById("dataList")!!
            .firstElementChild()!!
        // #dataList > tbody > tr
        val trs = tbody.children()

        val exams = mutableListOf<Exam>()

        // 排除表头，每一行为一个课程的考试信息
        for (index in 1..<trs.size) {
            // #dataList > tbody > tr > td
            val tds = trs[index].children()
            // 考试时间
            val time = tds[index + 3].text().split("~")
            val startTime = LocalDateTime.parseIsoVariantWithoutSecond(time[0])
            val endTime = LocalDateTime.parseIsoVariantWithoutSecond(time[1])
            // 校区
            val campus = Campus.getByName(tds[index + 4].text())

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

    override suspend fun getPlans() = withContext(Dispatchers.Default) {
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

    override suspend fun getProgresses() = withContext(Dispatchers.Default) {
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

    override suspend fun getTeachers(
        query: String,
        page: Int,
    ) = withContext(Dispatchers.Default) {
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
            teachers = teachers,
        )
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

    override suspend fun getTeacher(
        id: String,
    ) = withContext(Dispatchers.Default) {
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

    private inline fun Elements.parseSimpleCourses(
        index: Int,
        action: (Teacher.Course) -> Unit,
    ) {
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

    override suspend fun getCalendar(
        semester: Semester,
    ) = withContext(Dispatchers.Default) {
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

        val start = LocalDate.parse(startTdTitle)
        val end = LocalDate.parse(endTdTitle)

        return@withContext start..end
    }

    override suspend fun getAlerts(
        page: Int,
    ) = withContext(Dispatchers.Default) {
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

    override suspend fun getCourseGrades() = withContext(Dispatchers.Default) {
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

    override suspend fun getLevelGrades() = withContext(Dispatchers.Default) {
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

    override suspend fun getGraduationAudit(
        isPrimary: Boolean,
    ) = withContext(Dispatchers.Default) {
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
        val reportUrl = tds[8].selectFirst(
            Evaluator.Tag("a"),
        )!!.attr("href").getUrlFromInsetJavaScript()

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

    override suspend fun getExemptions(
        semester: Semester,
    ) = withContext(Dispatchers.Default) {
        val text = httpClient.submitForm(
            "/jsxsd/kscj/mtsq_list",
            parameters {
                append("xnxqid", "$semester")
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

    override suspend fun getCourseSystem(
        token: String?,
    ) = withContext(Dispatchers.Default) {
        token?.let {
            val text = httpClient.get("/jsxsd/xsxk/xsxk_index") {
                parameter("jx0502zbid", it)
            }.bodyAsText()

            val body = Ksoup.parse(text).body()

            check(body.childrenSize() != 0) { body.text().trim() }
            return@withContext CourseSystem(it)
        }

        val text = httpClient.get("/jsxsd/xsxk/xklc_list")
            .bodyAsText()

        val doc = Ksoup.parse(text)

        // #tbKxkc > tbody
        val tbody = doc.getElementById("tbKxkc")
            ?.firstElementChild()!!

        check(tbody.childrenSize() == 2)
        // #tbKxkc > tbody > tr:nth-child(2)
        val lastTr = tbody.lastElementChild()!!
        // #tbKxkc > tbody > tr:nth-child(2) > td
        val tds = lastTr.children()

        // #tbKxkc > tbody > tr:nth-child(2) >
        // td:nth-child(7) > a:nth-child(1)
        // “进入选课”按钮
        val urlString = tds[6].firstElementChild()!!
            .attr("href")
        val response = httpClient.get(urlString)
        val body = Ksoup.parse(response.bodyAsText()).body()

        check(body.childrenSize() != 0) { body.text().trim() }

        val startTime = tds[2].text().let {
            LocalDateTime.parseIsoVariantWithoutSecond(it)
        }
        val endTime = tds[3].text().let {
            LocalDateTime.parseIsoVariantWithoutSecond(it)
        }

        return@withContext CourseSystem(
            token = parseQueryString(
                response.request.url.encodedQuery,
            )["jx0502zbid"]!!,
            name = tds[1].text(),
            startTime = startTime,
            endTime = endTime,
        )
    }

    override suspend fun getCourses(
        category: SelectableCourse.Category.Special,
        page: Int,
        size: Int,
    ) = getCourses(category, page, size) {}

    override suspend fun getCourses(
        category: SelectableCourse.Category.Common,
        parameters: SelectableCourse.Parameters,
        page: Int,
        size: Int,
    ) = getCourses(category, page, size) {
        with(parameters) {
            parameter("kcxx", name.encodeURLParameter())
            parameter("skls", teacher.encodeURLParameter())
            parameter("skxq", dayOfWeek?.isoDayNumber ?: "")
            parameter("skjc", sectionPair?.let { "$it-" } ?: "")
            parameter("sfym", filterFull)
            parameter("sfct", filterConflict)
            if (category is SelectableCourse.Category.Common.General) {
                parameter("xq", campus?.id ?: "")
            }
        }
    }

    private suspend fun getCourses(
        category: SelectableCourse.Category,
        page: Int,
        size: Int,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = withContext(Dispatchers.Default) {
        return@withContext httpClient.submitForm(
            "/jsxsd/xsxkkc/xsxk${category.routeSegment}xk",
            parameters {
                append("iDisplayStart", "${(page - 1) * size}")
                append("iDisplayLength", "$size")
            },
            block = block,
        ).run {
            Json.decodeFromString<CoursesBody>(bodyAsText())
        }.list.map { it.asSelectableCourse(category) }
    }

    /**
     * 课程响应数据。
     *
     * @property list 课程列表
     */
    @Serializable
    data class CoursesBody(
        @SerialName("aaData") val list: List<CourseData>,
    )

    /**
     * 课程响应数据。
     *
     * @property id 课程唯一标识
     * @property courseId 课程编号
     * @property name 课程名称
     * @property credits 学分
     * @property teacher 教师
     * @property campusId 校区 id
     * @property time 上课时间。格式为“周次 星期 节次”
     * @property classroom 教室
     * @property note 备注
     * @property conflict 选课冲突情况
     * @property category 课程类别
     * @property isSelectable 是否开放选课。0为不开放，1为开放
     * @property isSelected 是否已选。0为未选，1为已选
     * @property department 开课单位
     * @property assessment 考核方式
     * @property arrangements 上课安排
     * @property total 课程总量
     * @property remain 课程剩余量
     */
    @Serializable
    data class CourseData(
        @SerialName("jx0404id") val id: String,
        @SerialName("kch") val courseId: String,
        @SerialName("kcmc") val name: String,
        @SerialName("xf") val credits: Double,
        @SerialName("skls") val teacher: String,
        @SerialName("xqid") val campusId: Int,
        @SerialName("sksj") val time: String,
        @SerialName("skdd") val classroom: String,
        @SerialName("bj") val note: String?,
        @SerialName("ctsm") val conflict: String? = null,
        // 该项暂时不需要
        // @SerialName("szkcflmc") val category: String?,
        @SerialName("sfkfxk") val isSelectable: Int,
        @SerialName("sfYx") val isSelected: Int,
        @SerialName("dwmc") val department: String,
        @SerialName("ksfs") val assessment: String,
        @SerialName("kkapList") val arrangements: List<ArrangementData>? = null,
        @SerialName("xxrs") val total: Int,
        @SerialName("syrs") val remain: Int,
    ) {
        /**
         * 转化为 [SelectableCourse]。
         */
        fun asSelectableCourse(category: SelectableCourse.Category) = SelectableCourse(
            id = id,
            courseId = courseId,
            name = name,
            credits = credits,
            teacher = teacher,
            campus = Campus.getById(campusId),
            time = time.takeIf { it.isNotEmptyHtmlText() },
            classroom = classroom.takeIf { it.isNotEmptyHtmlText() },
            note = note.takeIf { it != "拟开课时间:" },
            conflict = conflict.takeIf { it?.isNotEmpty() == true },
            category = category,
            isSelectable = isSelectable == 1,
            isSelected = isSelected == 1,
            department = department,
            assessment = assessment,
            arrangements = arrangements?.map { it.asArrangement() },
            total = total,
            remain = remain,
        )

        /**
         * 判断字符串是否不为空 HTML 字符串。
         */
        private fun String.isNotEmptyHtmlText() =
            this != "&nbsp;" || this.isNotEmpty()
    }

    /**
     * 上课安排响应数据。
     *
     * @property classroom 教室
     * @property weeks 周次
     * @property isoDayNumber 星期数字
     * @property sections 节次
     */
    @Serializable
    data class ArrangementData(
        @SerialName("jsmc") val classroom: String,
        @SerialName("kkzc") val weeks: String,
        @SerialName("xq") val isoDayNumber: Int,
        @SerialName("skjcmc") val sections: String,
    ) {
        /**
         * 转化为 [SelectableCourse.Arrangement]
         */
        fun asArrangement() = SelectableCourse.Arrangement(
            classroom = classroom,
            weeks = weeks,
            dayOfWeek = DayOfWeek(isoDayNumber),
            sections = sections.parseAsSections(),
        )
    }

    override suspend fun selectCourse(
        id: String,
        category: SelectableCourse.Category,
        priority: SelectableCourse.Priority?,
    ) = withContext(Dispatchers.Default) {
        val body = httpClient.get(
            "/jsxsd/xsxkkc/${category.routeSegment.lowercase()}xkOper",
        ) {
            parameter("jx0404id", id)
            // 志愿，1 第一志愿，2 第二志愿，3 第三志愿
            parameter("xkzy", priority?.value ?: "")
            parameter("trjf", "")
            parameter("cxxdlx", "1")
        }.run {
            Json.decodeFromString<CourseOperateData>(bodyAsText())
        }

        check(body.isSuccess) { body.message }
    }

    /**
     * 课程操作响应数据。
     *
     * @property isSuccess 操作是否成功
     * @property message 消息
     */
    @Serializable
    data class CourseOperateData(
        @SerialName("success") val isSuccess: Boolean,
        val message: String = "",
    )

    override suspend fun deleteCourse(
        id: String,
    ) = withContext(Dispatchers.Default) {
        val body = httpClient.get("/jsxsd/xsxkjg/xstkOper") {
            parameter("jx0404id", id)
        }.run {
            Json.decodeFromString<CourseOperateData>(bodyAsText())
        }

        check(body.isSuccess) { body.message }
    }

    override suspend fun getCoursesOverview() = withContext(
        Dispatchers.Default,
    ) {
        val text = httpClient.get("/jsxsd/xsxk/xsxk_tzsm")
            .bodyAsText()

        val doc = Ksoup.parse(text)
        val tbody = doc.selectFirst(Evaluator.Tag("tbody"))!!
        val trs = tbody.children()

        val progresses = mutableListOf<CoursesOverview.Progress>()

        val tds = trs.map { it.children() }
        val trSize = trs.first()!!.childrenSize()

        // 该表格需要从左往右逐个从上往下读
        for (index in 1..<trSize) {
            progresses.add(
                CoursesOverview.Progress(
                    // 位于第一行
                    name = tds[0][index].text(),
                    // 位于第三行
                    credits = tds[2][index].text(),
                    // 位于第二行
                    limitCredits = tds[1][index].text(),
                )
            )
        }

        return@withContext CoursesOverview(
            progress = progresses,
            note = doc.selectFirst(Evaluator.Tag("div"))!!.text(),
        )
    }

    override suspend fun getSelectedCourses() = withContext(
        Dispatchers.Default,
    ) {
        val text = httpClient.get("/jsxsd/xsxkjg/comeXkjglb")
            .bodyAsText()

        val doc = Ksoup.parse(text)
        val tbody = doc.selectFirst(Evaluator.Tag("tbody"))!!
        val trs = tbody.children()

        val courses = mutableListOf<SelectedCourse>()

        for (tr in trs) {
            val tds = tr.children()

            courses.add(
                SelectedCourse(
                    id = tds[8].firstElementChild()!!
                        .attr("href")
                        // TODO: 使用该方法并借助 Ktor Url 工具解析
                        // .getUrlFromInsetJavaScript()
                        .substring(21, 36),
                    courseId = tds[0].text(),
                    name = tds[1].text(),
                    credits = tds[2].text().toDouble(),
                    category = tds[3].text(),
                    teacher = tds[4].text(),
                    time = tds[5].text().takeIf { it.isNotEmpty() },
                    classroom = tds[6].text().takeIf { it.isNotEmpty() },
                )
            )
        }

        return@withContext courses
    }

    override suspend fun getCourseLogs() = withContext(
        Dispatchers.Default,
    ) {
        val text = httpClient.get("/jsxsd/xsxkjg/getTkrzList")
            .bodyAsText()

        val doc = Ksoup.parse(text)
        val tbody = doc.selectFirst(Evaluator.Tag("tbody"))!!
        val trs = tbody.children()

        val logs = mutableListOf<CourseLog>()

        for (tr in trs) {
            val tds = tr.children()

            logs.add(
                CourseLog(
                    courseId = tds[0].text(),
                    courseName = tds[1].text(),
                    credits = tds[2].text().toDouble(),
                    property = tds[3].text(),
                    teacher = tds[4].text(),
                    time = tds[5].run {
                        if (text().isBlank()) emptyList()
                        else textNodes().map { textNode -> textNode.text() }
                    },
                    category = tds[6].text(),
                    action = tds[7].text(),
                    operateTime = tds[8].text(),
                    operator = tds[9].text(),
                    description = tds[10].text(),
                )
            )
        }

        return@withContext logs
    }
}

/** 数字正则 */
private val numberRegex by lazy { Regex("\\d+") }

/**
 * 将字符串解析为节次集合。
 */
private fun String.parseAsSections() = numberRegex
    .findAll(this)
    .map { it.value.toInt() }
    .toSet()

/** Html 内嵌 JavaScript 的 Url 提取正则 */
private val insetJavaScriptUrlRegex by lazy {
    Regex("\\('([^']*)'\\)")
}

/**
 * 从内嵌 JavaScript 中提取 Url。
 */
private fun String.getUrlFromInsetJavaScript(): String {
    return insetJavaScriptUrlRegex
        .find(this)!!
        .groupValues[1]
}