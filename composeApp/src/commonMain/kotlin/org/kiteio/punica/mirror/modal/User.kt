package org.kiteio.punica.mirror.modal

/**
 * 用户。
 *
 * @property id 学号
 * @property password 门户密码
 * @property secondClassroomPwd 第二课堂密码
 */
data class User(
    val id: String,
    val password: String,
    val secondClassroomPwd: String,
)