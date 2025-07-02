import kotlinx.coroutines.runBlocking
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.kiteio.punica.mirror.modal.User
import org.kiteio.punica.mirror.modal.education.Course
import org.kiteio.punica.mirror.modal.education.Semester
import org.kiteio.punica.mirror.modal.education.containsWeek
import org.kiteio.punica.mirror.platform.readText
import org.kiteio.punica.mirror.service.EducationService
import util.readProperties
import kotlin.test.Test
import kotlin.test.assertEquals

class EducationServiceTest {
    private val service = EducationService()
    private val user = run {
        val properties = readProperties()
        User(
            id = properties["user.id"]!!,
            password = properties["user.password"]!!,
            secondClassPwd = "",
        )
    }

    init {
        login()
    }

    /**
     * 登录。
     */
    private fun login(): Unit = runBlocking {
        val dataPath = "src/commonTest/resources/tessdata/"
        try {
            val captcha = service.getCaptcha().readText(dataPath)
            service.login(user, captcha)
        } catch (e: Exception) {
            // 验证码出错重试
            if (e.message == "验证码错误!!") login()
        }
    }

    /**
     * 登出测试。
     */
    @Test
    fun shouldLogout(): Unit = runBlocking {
        service.logout()
    }

    /**
     * 获取课表测试。
     */
    @Test
    fun shouldGetTimetable(): Unit = runBlocking {
        println(service.getTimetable(Semester(2021, Semester.Term.First)))
    }

    /**
     * 获取课程课表测试。
     */
    @Test
    fun shouldGetCourseTable(): Unit = runBlocking {
        println(service.getCourseTable(Semester.now))
    }

    /**
     * 周次匹配测试。
     */
    @Test
    fun shouldMatchWeek() {
        val week = 4
        val mappedResult = mapOf(
            "1-16周" to true,
            "1-16（周）" to true,
            "1-16(周)" to true,
            "1-16单周" to false,
            "1-16（单周）" to false,
            "1-16双周" to true,
            "1-16（双周）" to true,
            "2，3, 4周" to true,
            "2, 3, 4周" to true,
            "1, 3-4单周" to false,
        )
        for ((weeks, result) in mappedResult) {
            val course = Course.Builder().apply {
                name = "移动应用开发"
                dayOfWeek = DayOfWeek.THURSDAY
                this.weeks = weeks
            }.build()
            println(weeks)
            assertEquals(result, course.containsWeek(week))
        }
    }

    /**
     * 获取考试安排测试。
     */
    @Test
    fun shouldGetExams(): Unit = runBlocking {
        println(service.getExams())
    }

    /**
     * 获取执行计划测试。
     */
    @Test
    fun shouldGetPlans(): Unit = runBlocking {
        println(service.getPlans())
    }

    /**
     * 获取课程进度测试。
     */
    @Test
    fun shouldGetProgresses(): Unit = runBlocking {
        println(service.getProgresses())
    }

    /**
     * 获取教师列表测试。
     */
    @Test
    fun shouldGetTeachers(): Unit = runBlocking {
        println(service.getTeachers("abcdefg"))
    }

    /**
     * 获取教师测试。
     */
    @Test
    fun shouldGetTeacher(): Unit = runBlocking {
        println(service.getTeacher("20191180"))
    }

    /**
     * 获取学期时间测试。
     */
    @Test
    fun shouldGetCalendar(): Unit = runBlocking {
        assertEquals(
            service.getCalendar(Semester.parse("2024-2025-2")),
            LocalDate(2025, 3, 2)..
                    LocalDate(2025, 7, 12),
        )
    }

    /**
     * 获取学籍预警测试。
     */
    @Test
    fun shouldGetAlerts(): Unit = runBlocking {
        println(service.getAlerts())
    }

    /**
     * 获取课程成绩测试。
     */
    @Test
    fun shouldGetCourseGrades(): Unit = runBlocking {
        println(service.getCourseGrades())
    }

    /**
     * 获取等级成绩测试。
     */
    @Test
    fun shouldGetLevelGrades(): Unit = runBlocking {
        println(service.getLevelGrades())
    }

    /**
     * 获取毕业审核测试。
     */
    @Test
    fun shouldGetGraduationAudit(): Unit = runBlocking {
        println(service.getGraduationAudit())
    }

    /**
     * 获取培养方案测试。
     */
    @Test
    fun shouldGetProgramme(): Unit = runBlocking {
        println(service.getProgramme())
    }

    /**
     * 获取免听申请测试。
     */
    @Test
    fun shouldGetExemptions(): Unit = runBlocking {
        println(
            service.getExemptions(
                Semester(2024, Semester.Term.First),
            )
        )
    }

    /**
     * 获取选课系统测试。
     */
    @Test
    fun shouldGetCourseSystem(): Unit = runBlocking {
        println(service.getCourseSystem(null))
    }
}