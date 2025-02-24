package org.kiteio.punica.ui.page.account

import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.getString
import org.kiteio.punica.client.academic.foundation.User
import org.kiteio.punica.serialization.*
import org.kiteio.punica.ui.page.account.AccountCategory.*
import org.kiteio.punica.ui.widget.showToast
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.remove_current_account
import punica.composeapp.generated.resources.save_successful
import punica.composeapp.generated.resources.set_up_current_account

class AccountVM(val category: AccountCategory) : ViewModel() {
    /** 用户 */
    val users = Stores.users.data.deserializeToList<User>().run {
        when (category) {
            // 所有用户
            Network -> map { list -> list.sortedBy { it.id } }
            // 仅 userId 为 11 位的用户
            else -> map { list ->
                list.filter { it.id.length == 11 }.sortedBy { it.id }
            }
        }
    }

    private val key = when (category) {
        Academic -> PrefsKeys.ACADEMIC_USER_ID
        SecondClass -> PrefsKeys.SECOND_CLASS_USER_ID
        Network -> PrefsKeys.NETWORK_USER_ID
    }


    /**
     * 保存用户。
     */
    suspend fun saveAccount(parameters: AccountParameters) {
        Stores.users.edit { it[parameters.user.id] = parameters.user }
        // 更新用户
        if (parameters.loginWhenSave) setupCurrentAccount(parameters.user.id)

        showToast(getString(Res.string.save_successful))
    }


    /**
     * 设为当前账号。
     */
    suspend fun setupCurrentAccount(userId: String) {
        Stores.prefs.edit { it[key] = userId }
        showToast(getString(Res.string.set_up_current_account))
    }


    /**
     * 移除当前账号。
     */
    suspend fun removeCurrentAccount() {
        Stores.prefs.edit { it.remove(key) }
        showToast(getString(Res.string.remove_current_account))
    }
}