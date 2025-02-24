package org.kiteio.punica.ui.page.account

import org.kiteio.punica.client.academic.foundation.User

/**
 * 账号参数。
 *
 * @param user 用户
 * @param loginWhenSave 保存时登录
 */
data class AccountParameters(
    val user: User,
    val loginWhenSave: Boolean,
)