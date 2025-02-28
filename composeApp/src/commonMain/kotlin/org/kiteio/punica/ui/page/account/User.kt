package org.kiteio.punica.ui.page.account

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.foundation.User
import org.kiteio.punica.tool.TOTP
import org.kiteio.punica.ui.widget.showToast
import org.kiteio.punica.wrapper.LaunchedEffectCatching
import org.kiteio.punica.wrapper.launchCatching
import org.kiteio.punica.wrapper.now
import punica.composeapp.generated.resources.*


/**
 * OTP 用户。
 */
@Composable
fun AccountVM.OTPUser(user: User, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    val otp = remember { TOTP(secret = user.otpSecret) }
    var password by remember { mutableStateOf(otp.generate()) }
    var leftSecond by remember {
        mutableIntStateOf(
            LocalDateTime.now().leftSecond()
        )
    }

    LaunchedEffectCatching(Unit) {
        repeat(Int.MAX_VALUE) {
            delay(500)
            password = otp.generate()
            leftSecond = LocalDateTime.now().leftSecond()
        }
    }

    ElevatedCard(onClick = onClick, modifier = modifier) {
        ListItem(
            headlineContent = {
                Text(
                    if (password.length == 6) password.run {
                        "${substring(0..2)} ${substring(3..5)}"
                    } else password,
                    color = if (leftSecond <= 5) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            supportingContent = { Text("${user.id}    ${leftSecond}s") },
            trailingContent = {
                Row {
                    IconButton(
                        onClick = {
                            scope.launchCatching {
                                val text = buildAnnotatedString { append(password) }
                                clipboardManager.setText(text)
                                showToast(getString(Res.string.copy_successful))
                            }
                        }
                    ) {
                        Icon(
                            Icons.Outlined.ContentCopy,
                            contentDescription = stringResource(Res.string.copy)
                        )
                    }

                    DeleteIconButton(
                        onDeleteAccount = { scope.launchCatching { AppVM.deleteUser(type, user.id) } },
                    )
                }
            }
        )
    }
}


/**
 * 返回距离0或30秒的秒数。
 */
private fun LocalDateTime.leftSecond() = second.let {
    if (it in 0..29) 30 - it else 60 - it
}


@Composable
fun AccountVM.User(user: User, isCurrentAccount: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()

    ElevatedCard(onClick = onClick, modifier = modifier) {
        ListItem(
            headlineContent = {
                Text(
                    user.id,
                    color = if (isCurrentAccount) MaterialTheme.colorScheme.primary
                    else LocalContentColor.current,
                )
            },
            trailingContent = {
                Row {
                    // 设为、移除当前账号
                    IconButton(
                        onClick = {
                            scope.launchCatching {
                                if (isCurrentAccount) removeCurrentAccount()
                                else setupCurrentAccount(user.id)
                            }
                        },
                    ) {
                        Icon(
                            if (isCurrentAccount) Icons.Filled.LocalOffer
                            else Icons.Outlined.LocalOffer,
                            contentDescription = stringResource(
                                if (isCurrentAccount) Res.string.remove_current_account
                                else Res.string.set_up_current_account,
                            ),
                        )
                    }

                    DeleteIconButton(
                        onDeleteAccount = { scope.launchCatching { AppVM.deleteUser(type, user.id) } },
                    )
                }
            }
        )
    }
}


/**
 * 删除账号
 */
@Composable
private fun DeleteIconButton(onDeleteAccount: () -> Unit) {
    IconButton(onClick = onDeleteAccount) {
        Icon(
            Icons.Outlined.DeleteOutline,
            contentDescription = stringResource(Res.string.delete),
        )
    }
}