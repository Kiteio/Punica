package org.kiteio.punica.mirror.modal.notice

/**
 * 教务通知。
 *
 * @property title 标题
 * @property time 发布时间
 * @property urlString Url
 */
data class Notice(
    val title: String,
    val time: String,
    val urlString: String,
)