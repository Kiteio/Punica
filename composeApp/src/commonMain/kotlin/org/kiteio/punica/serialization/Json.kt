package org.kiteio.punica.serialization

import kotlinx.serialization.json.Json

/** 返回默认配置的 [kotlinx.serialization.json.Json]。 */
fun Json() = Json {
    // 美化打印
    prettyPrint = true
    // 宽松模式
    isLenient = true
    // 忽略未知字段
    ignoreUnknownKeys = true
}