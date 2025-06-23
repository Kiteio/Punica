import kotlinx.coroutines.runBlocking
import org.kiteio.punica.mirror.modal.User
import org.kiteio.punica.mirror.modal.secondclass.BasicActivity
import org.kiteio.punica.mirror.service.SecondClassService
import util.readProperties
import kotlin.test.Test

class SecondClassServiceTest {
    private val service = SecondClassService()
    private val user = run {
        val properties = readProperties()
        User(
            id = properties["user.id"]!!,
            password = properties["user.password"]!!,
            secondClassPwd = "",
        )
    }

    init {
        runBlocking { service.login(user) }
    }

    /**
     * 获取成绩单测试。
     */
    @Test
    fun shouldGetGrades(): Unit = runBlocking {
        println(service.getGrades())
    }

    /**
     * 获取成绩日志测试。
     */
    @Test
    fun shouldGetGradeLogs(): Unit = runBlocking {
        println(service.getGradeLogs())
    }

    /**
     * 获取活动列表测试。
     */
    @Test
    fun shouldGetActivities(): Unit = runBlocking {
        println(service.getActivities())
    }

    /**
     * 获取我的活动列表测试。
     */
    @Test
    fun shouldGetMyActivities(): Unit = runBlocking {
        println(service.getMyActivities(BasicActivity.State.Enrolled))
    }

    /**
     * 获取活动详情测试。
     */
    @Test
    fun shouldGetActivity(): Unit = runBlocking {
        println(service.getActivity(7645))
    }
}