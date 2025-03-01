package org.kiteio.punica.ui.page.settings

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.ui.compositionlocal.LocalNavController
import org.kiteio.punica.ui.page.account.AccountRoute
import org.kiteio.punica.ui.page.account.PasswordType
import org.kiteio.punica.ui.page.account.PasswordType.*
import org.kiteio.punica.ui.component.SettingsGroup
import org.kiteio.punica.ui.component.SettingsMenuLink
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.account
import punica.composeapp.generated.resources.not_logged_in

/**
 * 账号设置。
 */
@Composable
fun AccountSettingsGroup() {
    val navController = LocalNavController.current

    SettingsGroup(title = { Text(stringResource(Res.string.account)) }) {
        PasswordType.entries.forEach {
            val userId by when (it) {
                Academic -> AppVM.academicUserId
                SecondClass -> AppVM.secondClassUserId
                Network -> AppVM.networkUserId
                OTP -> flowOf(null)
            }.collectAsState(null)

            SettingsMenuLink(
                title = { Text(stringResource(it.nameRes)) },
                subtitle = if (it == OTP) null else {
                    { Text(userId ?: stringResource(Res.string.not_logged_in)) }
                },
                icon = {
                    Icon(
                        it.icon,
                        contentDescription = stringResource(it.nameRes),
                    )
                },
                onClick = { navController.navigate(AccountRoute(it.ordinal)) }
            )
        }
    }
}