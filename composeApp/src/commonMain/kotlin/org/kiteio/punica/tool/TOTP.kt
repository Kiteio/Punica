package org.kiteio.punica.tool

/**
 * Time-Based One-Time Password（TOTP）。
 */
interface TOTP {
    /**
     * 返回 TOTP 密码。
     */
    fun generate(): String
}


/**
 * 返回密钥为 [secret] 的 [TOTP]。
 */
expect fun TOTP(secret: String): TOTP