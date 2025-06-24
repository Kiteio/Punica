import kotlinx.coroutines.runBlocking
import org.kiteio.punica.mirror.service.BingService
import kotlin.test.Test

class BingServiceTest {
    private val service = BingService()

    @Test
    fun shouldGetWallpaper(): Unit = runBlocking {
        println(service.getWallpaper())
    }
}