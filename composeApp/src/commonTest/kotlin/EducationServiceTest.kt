import io.ktor.utils.io.readText
import kotlinx.coroutines.runBlocking
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import org.kiteio.punica.mirror.modal.User
import org.kiteio.punica.mirror.modal.education.Semester
import org.kiteio.punica.mirror.modal.education.Term
import org.kiteio.punica.mirror.service.EducationService
import org.kiteio.punica.mirror.util.readText
import kotlin.test.Test

class EducationServiceTest {
    private val service = EducationService()
    private val user = run {
        val properties = hashMapOf<String, String>()
        val text = SystemFileSystem
            .source(Path("../local.properties"))
            .buffered()
            .readText()
        text.split("\n").forEach {
            if (!it.startsWith('#') && it.isNotEmpty()) {
                val (key, value) = it.split("=")
                properties[key.trim()] = value.trim()
            }
        }
        User(
            id = properties["user.id"]!!,
            password = properties["user.password"]!!,
            secondClassroomPwd = "",
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
            val captcha = service.captcha().readText(dataPath)
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
        println(service.timetable(Semester(2021, Term.FIRST)))
    }
}