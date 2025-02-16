package org.kiteio.punica.client.secondclass.foundation

/**
 * 第二课堂响应内容。
 *
 * @property code 状态码
 * @property msg 状态消息
 * @property data 数据
 */
interface SecondClassBody<T> {
    val code: Int
    val msg: String
    val data: T
}