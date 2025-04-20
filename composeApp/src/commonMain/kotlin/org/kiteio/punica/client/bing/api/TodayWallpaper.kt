package org.kiteio.punica.client.bing.api

import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.bing.Bing

/**
 * 返回今日壁纸 url。
 */
suspend fun Bing.getTodayWallpaper() = get("/HPImageArchive.aspx") {
    parameter("format", "js")
    parameter("n", 1)
    header(HttpHeaders.AcceptCharset, "UTF-8")
    header(HttpHeaders.Accept, "application/json; charset=UTF-8")
}.body<WallpaperBody>().images[0].url.let {
    "$baseUrl$it"
}


@Serializable
private data class WallpaperBody(
    val images: List<Wallpaper>,
)


@Serializable
private data class Wallpaper(
    val url: String,
)