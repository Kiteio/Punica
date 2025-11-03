package service

import kotlinx.coroutines.runBlocking
import org.kiteio.punica.mirror.service.getBingService
import kotlin.test.Test

class BingServiceTest {
    private val service = getBingService()

    /**
     * 获取壁纸测试。
     */
    @Test
    fun shouldGetWallpapers(): Unit = runBlocking {
        println(service.getWallpapers())
    }
}