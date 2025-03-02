package org.kiteio.punica.ui.page.settings

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.ui.component.SettingsGroup
import org.kiteio.punica.ui.component.SettingsMenuLink
import org.kiteio.punica.ui.compositionlocal.LocalNavController
import org.kiteio.punica.ui.page.account.AccountRoute
import org.kiteio.punica.ui.page.account.PasswordType
import org.kiteio.punica.ui.page.account.PasswordType.*
import org.kiteio.punica.wrapper.launchCatching
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.account
import punica.composeapp.generated.resources.login
import punica.composeapp.generated.resources.not_logged_in

/**
 * 账号设置。
 */
@Composable
fun AccountSettingsGroup() {
    val scope = rememberCoroutineScope()
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
                subtitle = when (it) {
                    OTP -> null
                    Academic -> {
                        {
                            Text(
                                userId?.let {
                                    if (AppVM.academicSystem == null) "$it  ${stringResource(Res.string.not_logged_in)}"
                                    else it
                                } ?: stringResource(Res.string.not_logged_in))
                        }
                    }

                    else -> {
                        { Text(userId ?: stringResource(Res.string.not_logged_in)) }
                    }
                },
                icon = {
                    Icon(
                        it.icon,
                        contentDescription = stringResource(it.nameRes),
                    )
                },
                action = if (it == Academic && userId != null && AppVM.academicSystem == null) {
                    {
                        TextButton(
                            onClick = {
                                scope.launchCatching { AppVM.updateAcademicSystem(userId) }
                            },
                        ) { Text(stringResource(Res.string.login)) }
                    }
                } else null,
                onClick = { navController.navigate(AccountRoute(it.ordinal)) }
            )
        }
    }
}