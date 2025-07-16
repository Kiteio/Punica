package org.kiteio.punica.mirror.modal.bing

import kotlinx.serialization.Serializable

/**
 * Bing 壁纸。
 *
 * @property title 标题
 * @property url 图片 URL
 */
@Serializable
data class Wallpaper(
    val title: String,
    val url: String,
)