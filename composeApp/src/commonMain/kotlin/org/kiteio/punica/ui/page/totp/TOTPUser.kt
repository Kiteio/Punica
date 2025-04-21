package org.kiteio.punica.ui.page.totp

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.serialization.Stores
import org.kiteio.punica.serialization.remove
import org.kiteio.punica.tool.TOTP
import org.kiteio.punica.tool.TOTPUser
import org.kiteio.punica.ui.component.CardListItem
import org.kiteio.punica.ui.component.showToast
import org.kiteio.punica.wrapper.LaunchedEffectCatching
import org.kiteio.punica.wrapper.launchCatching
import org.kiteio.punica.wrapper.now
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.copy
import punica.composeapp.generated.resources.copy_successful
import punica.composeapp.generated.resources.delete


/**
 * TOTP 用户。
 */
@Composable
fun TOTPUser(tOTPUser: TOTPUser, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    val otp = remember { TOTP(secret = tOTPUser.secret) }
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

    CardListItem(
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
        onClick = onClick,
        modifier = modifier,
        supportingContent = { Text("${tOTPUser.name}    ${leftSecond}s") },
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
                    onDeleteAccount = {
                        scope.launchCatching {
                            Stores.tOTPUsers.edit { it.remove(tOTPUser.name) }
                        }
                    },
                )
            }
        }
    )
}


/**
 * 返回距离0或30秒的秒数。
 */
private fun LocalDateTime.leftSecond() = second.let {
    if (it in 0..29) 30 - it else 60 - it
}


/**
 * 删除账号。
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