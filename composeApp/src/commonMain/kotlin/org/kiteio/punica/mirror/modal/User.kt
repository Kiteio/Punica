package org.kiteio.punica.mirror.modal

/**
 * 用户。
 *
 * @property id 学号
 * @property password 门户密码
 * @property secondClassPwd 第二课堂密码
 */
data class User(
    val id: String,
    val password: String,
    val secondClassPwd: String,
)

/**
 * TOTP 用户。
 *
 * @property name 名称
 * @property secret 密钥
 */
data class TotpUser(
    val name: String,
    val secret: String,
)