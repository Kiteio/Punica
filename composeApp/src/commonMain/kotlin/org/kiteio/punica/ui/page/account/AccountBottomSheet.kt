package org.kiteio.punica.ui.page.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.client.academic.foundation.User
import org.kiteio.punica.ui.component.Checkbox
import org.kiteio.punica.ui.component.ModalBottomSheet
import org.kiteio.punica.ui.page.account.PasswordType.Academic
import org.kiteio.punica.ui.page.account.PasswordType.OTP
import org.kiteio.punica.ui.page.account.PasswordType.SecondClass
import org.kiteio.punica.wrapper.focusCleaner
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.default_is_user_id
import punica.composeapp.generated.resources.invisible
import punica.composeapp.generated.resources.login_when_save
import punica.composeapp.generated.resources.password
import punica.composeapp.generated.resources.save
import punica.composeapp.generated.resources.secret
import punica.composeapp.generated.resources.user_id
import punica.composeapp.generated.resources.visible
import punica.composeapp.generated.resources.way_to_change_password
import punica.composeapp.generated.resources.way_to_get_otp_secret

/**
 * 账号模态对话框。
 */
@Composable
fun AccountVM.AccountBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    initialUser: User?,
    initialLoginWhenSave: Boolean = false,
) {
    ModalBottomSheet(visible, onDismissRequest = onDismissRequest) {
        val scope = rememberCoroutineScope()
        val focusManager = LocalFocusManager.current

        var userId by remember { mutableStateOf(initialUser?.id ?: "") }
        var password by remember {
            mutableStateOf(
                when (type) {
                    Academic -> initialUser?.password
                    SecondClass -> initialUser?.secondClassPwd
                    OTP -> initialUser?.otpSecret
                } ?: ""
            )
        }
        // 保存时登录
        var loginWhenSave by remember { mutableStateOf(initialLoginWhenSave) }

        var passwordVisible by remember { mutableStateOf(false) }
        val isUserIdError = userId.isBlank() || userId.length != 11
        val isPasswordError = type != SecondClass && password.isBlank()

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
                .focusCleaner(focusManager),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Logo()
            Spacer(modifier = Modifier.height(48.dp))

            // 学号
            TextField(
                value = userId,
                onValueChange = {
                    userId = it.filter { char -> char.isDigit() }.run {
                        if (length > 11) substring(0..10)
                        else this
                    }
                },
                readOnly = initialUser != null,
                label = stringResource(Res.string.user_id),
                isError = isUserIdError,
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 密码
            TextField(
                value = password,
                onValueChange = { password = it },
                label = if (type == OTP) stringResource(Res.string.secret)
                else stringResource(Res.string.password),
                placeholder = when (type) {
                    SecondClass -> stringResource(Res.string.default_is_user_id)
                    else -> null
                },
                trailingIcon = {
                    // 密码可见性
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Outlined.Visibility
                            else Icons.Outlined.VisibilityOff,
                            contentDescription = stringResource(
                                if (passwordVisible) Res.string.visible
                                else Res.string.invisible
                            ),
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                isError = isPasswordError,
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (type == Academic) {
                // 保存时登录
                Checkbox(
                    checked = loginWhenSave,
                    onCheckedChange = { loginWhenSave = it },
                    label = { Text(stringResource(Res.string.login_when_save)) },
                    modifier = Modifier.fillMaxWidth(0.8f),
                )
            } else if (type == OTP) {
                // 如何获取 OTP 密钥
                Text(
                    stringResource(Res.string.way_to_get_otp_secret),
                    modifier = Modifier.fillMaxWidth(0.74f),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            if (type == Academic) {
                // 如何更改密码
                Text(
                    stringResource(Res.string.way_to_change_password),
                    modifier = Modifier.fillMaxWidth(0.74f),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // 保存
            ElevatedButton(
                onClick = {
                    scope.launch {
                        focusManager.clearFocus()
                        saveAccount(userId, password, loginWhenSave)
                        onDismissRequest()
                    }
                },
                enabled = userId.isNotBlank() && password.isNotBlank() && userId.length == 11,
            ) {
                Text(stringResource(Res.string.save))
            }
        }
    }
}


/**
 * 标识。
 */
@Composable
private fun AccountVM.Logo() {
    // 图标
    FilledIconToggleButton(
        checked = true,
        onCheckedChange = {},
        modifier = Modifier.size(64.dp),
    ) {
        Icon(
            type.icon,
            contentDescription = stringResource(type.nameRes),
            modifier = Modifier.size(40.dp),
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // 名称
    Text(stringResource(type.nameRes), color = MaterialTheme.colorScheme.primary)
}


/**
 * 文本框
 */
@Composable
private fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false,
    label: String,
    placeholder: String? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(0.8f),
        readOnly = readOnly,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(placeholder) } },
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        isError = isError,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(0.6f),
        ),
    )
}