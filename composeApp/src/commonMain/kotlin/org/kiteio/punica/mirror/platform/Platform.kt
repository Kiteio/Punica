package org.kiteio.punica.mirror.platform

/**
 * 平台。
 */
sealed class Platform {
    /** 桌面端 */
    data object Desktop : Platform()

    /** 移动端 */
    sealed class Mobile : Platform()

    /** Android */
    data object Android : Mobile()

    /** iOS */
    data object iOS : Mobile()
}