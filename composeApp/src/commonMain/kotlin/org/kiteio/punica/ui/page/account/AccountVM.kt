package org.kiteio.punica.ui.page.account

import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.getString
import org.kiteio.punica.client.academic.foundation.User
import org.kiteio.punica.serialization.*
import org.kiteio.punica.ui.page.account.PasswordType.*
import org.kiteio.punica.ui.component.showToast
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.remove_current_account
import punica.composeapp.generated.resources.save_successful
import punica.composeapp.generated.resources.set_up_current_account

class AccountVM(val type: PasswordType) : ViewModel() {
    /** 用户 */
    val users = Stores.users.data.deserializeToList<User>().run {
        map { list ->
            when (type) {
                // 所有用户
                Network -> list
                // OTP 密钥不为空的用户
                OTP -> list.filter { it.otpSecret.isNotBlank() }
                // 仅 userId 为 11 位的用户
                else -> list.filter { it.id.length == 11 }
            }.sortedBy { it.id }
        }
    }

    private val key = when (type) {
        Academic -> PrefsKeys.ACADEMIC_USER_ID
        SecondClass -> PrefsKeys.SECOND_CLASS_USER_ID
        Network -> PrefsKeys.NETWORK_USER_ID
        OTP -> null
    }


    /**
     * 保存用户。
     */
    suspend fun saveAccount(userId: String, password: String, loginWhenSave: Boolean) {
        // 获取本地用户并更新，若本地用户不存在则新建
        val user = (Stores.users.data.map { it.get<User>(userId) }.first() ?: User(userId)).run {
            when (type) {
                Academic -> copy(password = password)
                SecondClass -> copy(secondClassPwd = password)
                Network -> copy(networkPwd = password)
                OTP -> copy(otpSecret = password)
            }
        }
        // 保存用户
        Stores.users.edit { it[user.id] = user }
        // 更新用户
        if (loginWhenSave) setupCurrentAccount(userId)

        showToast(getString(Res.string.save_successful))
    }


    /**
     * 设为当前账号。
     */
    suspend fun setupCurrentAccount(userId: String) {
        Stores.prefs.edit { it[key!!] = userId }
        showToast(getString(Res.string.set_up_current_account))
    }


    /**
     * 移除当前账号。
     */
    suspend fun removeCurrentAccount() {
        Stores.prefs.edit { it.remove(key!!) }
        showToast(getString(Res.string.remove_current_account))
    }
}