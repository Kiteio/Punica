package org.kiteio.punica.mirror.service

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import jakarta.inject.Singleton
import kotlinx.serialization.Serializable
import org.kiteio.punica.mirror.modal.bing.Wallpaper
import org.kiteio.punica.mirror.platform.CacheStorage
import org.kiteio.punica.mirror.util.AppDirs
import org.kiteio.punica.mirror.util.Json

/**
 * 必应服务。
 */
@Singleton
fun BingService(): BingService {
    val httpClient = HttpClient {
        defaultRequest {
            url(BingServiceImpl.BASE_URL)
        }
        install(ContentNegotiation) {
            json(Json)
        }
        install(HttpCache) {
            publicStorage(
                CacheStorage(AppDirs.cacheDir("bing"))
            )
        }
    }

    return BingServiceImpl(httpClient)
}

/**
 * 必应服务。
 */
interface BingService {
    suspend fun getWallpapers(): List<Wallpaper>
}

// --------------- 实现 ---------------

private class BingServiceImpl(
    private val httpClient: HttpClient,
) : BingService {
    override suspend fun getWallpapers(): List<Wallpaper> {
        val body = httpClient.get("/HPImageArchive.aspx") {
            // json 格式
            parameter("format", "js")
            // 图片数量
            parameter("n", 1)
        }.body<WallpaperBody>()

        return body.images.map {
            Wallpaper(it.title, "${BASE_URL}${it.url}")
        }
    }

    @Serializable
    private data class WallpaperBody(val images: List<Wallpaper>)

    companion object {
        const val BASE_URL = "https://cn.bing.com"
    }
}