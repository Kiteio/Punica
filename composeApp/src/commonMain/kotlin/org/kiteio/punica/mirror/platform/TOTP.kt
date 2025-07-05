package org.kiteio.punica.mirror.platform

/**
 * TOTP（Time-Based One-Time Password）。
 */
interface TOTP {
    /**
     * 生成 TOTP 密码。
     */
    fun generate(): String
}

/**
 * [TOTP]（Time-Based One-Time Password）。
 *
 * @param secret 密钥
 */
expect fun TOTP(secret: String): TOTP

