package org.kiteio.punica.ui.page.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.client.academic.foundation.User
import org.kiteio.punica.ui.page.account.AccountCategory.*
import org.kiteio.punica.ui.widget.Checkbox
import punica.composeapp.generated.resources.*

/**
 * 账号模态对话框。
 *
 * @param category 账号分类
 * @param initialUser 初始用户
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: (AccountParameters) -> Unit,
    category: AccountCategory,
    initialUser: User?,
    initialLoginWhenSave: Boolean,
) {
    if (visible) {
        var userId by remember { mutableStateOf(initialUser?.id ?: "") }
        var password by remember {
            mutableStateOf(
                when (category) {
                    Academic -> initialUser?.password
                    SecondClass -> initialUser?.secondClassPwd
                    Network -> initialUser?.networkPwd
                } ?: ""
            )
        }
        // 保存时登录
        var loginWhenSave by remember { mutableStateOf(initialLoginWhenSave) }

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            val focusManager = LocalFocusManager.current

            Column(
                modifier = Modifier.fillMaxSize().padding(8.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { focusManager.clearFocus() },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // 图标
                FilledIconToggleButton(
                    checked = true,
                    onCheckedChange = {},
                    modifier = Modifier.size(64.dp),
                ) {
                    Icon(
                        category.icon,
                        contentDescription = stringResource(category.nameRes),
                        modifier = Modifier.size(40.dp),
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 名称
                Text(stringResource(category.nameRes), color = MaterialTheme.colorScheme.primary)

                Spacer(modifier = Modifier.height(48.dp))

                // 学号
                TextField(
                    value = userId,
                    onValueChange = { userId = it.filter { char -> char.isDigit() } },
                    label = { Text(stringResource(Res.string.user_id)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(0.6f),
                    ),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 密码
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(Res.string.password)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(0.6f),
                    ),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 保存时登录
                Checkbox(
                    checked = loginWhenSave,
                    onCheckedChange = { loginWhenSave = !loginWhenSave },
                    label = { Text(stringResource(Res.string.login_when_save)) },
                    modifier = Modifier.width(280.dp),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 保存
                ElevatedButton(
                    onClick = {
                        focusManager.clearFocus()

                        val user = (initialUser ?: User(userId)).run {
                            when (category) {
                                Academic -> copy(password = password)
                                SecondClass -> copy(secondClassPwd = password)
                                Network -> copy(networkPwd = password)
                            }
                        }
                        onConfirm(AccountParameters(user, loginWhenSave))
                    },
                    enabled = password.isNotBlank() &&
                            if (category == Network) true else userId.length == 11,
                ) {
                    Text(stringResource(Res.string.save))
                }
            }
        }
    }
}