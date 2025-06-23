package org.kiteio.punica.mirror.util

import kotlinx.serialization.json.Json

/**
 * [kotlinx.serialization.json.Json]。
 */
val Json by lazy {
    Json {
        // 美化打印
        prettyPrint = true
        // 宽松模式
        isLenient = true
        // 忽略未知字段
        ignoreUnknownKeys = true
    }
}