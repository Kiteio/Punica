import kotlinx.coroutines.runBlocking
import org.kiteio.punica.mirror.service.BingService
import kotlin.test.Test

class BingServiceTest {
    private val service = BingService()

    /**
     * 获取壁纸测试。
     */
    @Test
    fun shouldGetWallpapers(): Unit = runBlocking {
        println(service.getWallpapers())
    }
}