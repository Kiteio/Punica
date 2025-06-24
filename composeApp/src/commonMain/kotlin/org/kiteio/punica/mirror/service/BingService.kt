package org.kiteio.punica.mirror.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import org.kiteio.punica.mirror.util.Json

/**
 * 必应服务。
 */
fun BingService(): BingService {
    val httpClient = HttpClient {
        defaultRequest {
            url(BingServiceImpl.BASE_URL)
        }
        install(ContentNegotiation) {
            json(Json)
        }
    }

    return BingServiceImpl(httpClient)
}

/**
 * 必应服务。
 */
interface BingService {
    suspend fun getWallpaper(): String
}

// --------------- 实现 ---------------

private class BingServiceImpl(
    private val httpClient: HttpClient,
) : BingService {
    override suspend fun getWallpaper(): String {
        val body = httpClient.get("/HPImageArchive.aspx") {
            // json 格式
            parameter("format", "js")
            // 图片数量
            parameter("n", 1)
        }.body<WallpaperBody>()
        return "$BASE_URL/${body.images.first().url}"
    }

    @Serializable
    private data class WallpaperBody(val images: List<Wallpaper>)

    @Serializable
    private data class Wallpaper(val url: String)

    companion object {
        const val BASE_URL = "https://cn.bing.com"
    }
}