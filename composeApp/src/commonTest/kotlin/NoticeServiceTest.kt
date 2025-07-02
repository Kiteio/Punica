import kotlinx.coroutines.runBlocking
import org.kiteio.punica.mirror.service.NoticeService
import kotlin.test.Test

class NoticeServiceTest {
    private val service = NoticeService()

    /**
     * 获取通知列表测试。
     */
    @Test
    fun shouldGetNotices() = runBlocking {
        println(service.getNotices(1))
    }

    /**
     * 获取通知测试。
     */
    @Test
    fun shouldGetNotice() = runBlocking {
        val urlString = "https://jwc.gdufe.edu.cn//2025/0630/c4133a222940/page.htm"
        println(service.getNotice(urlString))
    }
}