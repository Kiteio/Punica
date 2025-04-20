package org.kiteio.punica.ui.page.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.foundation.User
import org.kiteio.punica.ui.component.ModalBottomSheet
import org.kiteio.punica.ui.component.showToast
import org.kiteio.punica.ui.theme.link
import org.kiteio.punica.wrapper.LaunchedEffectCatching
import org.kiteio.punica.wrapper.focusCleaner
import org.kiteio.punica.wrapper.launchCatching
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.academic_system
import punica.composeapp.generated.resources.academic_system_password
import punica.composeapp.generated.resources.default_is_user_id
import punica.composeapp.generated.resources.error_password_is_empty
import punica.composeapp.generated.resources.error_user_id_is_11_digits
import punica.composeapp.generated.resources.expand_less
import punica.composeapp.generated.resources.expand_more
import punica.composeapp.generated.resources.forget_password
import punica.composeapp.generated.resources.invisible
import punica.composeapp.generated.resources.logging_in
import punica.composeapp.generated.resources.login
import punica.composeapp.generated.resources.login_successful
import punica.composeapp.generated.resources.logo
import punica.composeapp.generated.resources.logout
import punica.composeapp.generated.resources.password
import punica.composeapp.generated.resources.punica
import punica.composeapp.generated.resources.safety_instruction
import punica.composeapp.generated.resources.second_class_password
import punica.composeapp.generated.resources.should_be_11_digits
import punica.composeapp.generated.resources.user_id
import punica.composeapp.generated.resources.visible

@Composable
fun UserBottomSheet(visible: Boolean, onDismissRequest: () -> Unit) {
    ModalBottomSheet(visible, onDismissRequest) {
        val focusManager = LocalFocusManager.current
        val scope = rememberCoroutineScope()

        var user by remember { mutableStateOf<User?>(null) }
        var userId by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var secondClassPwd by remember { mutableStateOf(user?.secondClassPwd ?: "") }
        var secondClassVisible by remember { mutableStateOf(false) }
        var isLoginButtonClicked by remember { mutableStateOf(false) }

        LaunchedEffectCatching(Unit) {
            AppVM.userFlow.first()?.let {
                user = it
                userId = it.id
                password = it.password
                secondClassPwd = it.secondClassPwd
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp).focusCleaner(focusManager),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            contentPadding = PaddingValues(16.dp),
        ) {
            // Logo
            item {
                Logo()
                Spacer(modifier = Modifier.height(16.dp))
            }
            // 标题
            item {
                Text(
                    stringResource(Res.string.academic_system),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            // 介绍
            item {
                Text(
                    stringResource(Res.string.safety_instruction),
                    modifier = Modifier.fillMaxWidth(0.8f),
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.labelSmall,
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
            // 学号
            item {
                ElevatedTextField(
                    value = userId,
                    onValueChange = {
                        userId = it.filter { char -> char.isDigit() }.run {
                            if (length > 11) substring(0..10)
                            else this
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.9f),
                    readOnly = AppVM.isLoggingIn,
                    label = { Text(stringResource(Res.string.user_id)) },
                    placeholder = {
                        Text(stringResource(Res.string.should_be_11_digits))
                    },
                    isError = isLoginButtonClicked && userId.length != 11,
                    errorText = {
                        Text(stringResource(Res.string.error_user_id_is_11_digits))
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            // 密码
            item {
                ElevatedPasswordTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(0.9f),
                    readOnly = AppVM.isLoggingIn,
                    label = { Text(stringResource(Res.string.password)) },
                    placeholder = { Text(stringResource(Res.string.academic_system_password)) },
                    isError = isLoginButtonClicked && password.isEmpty(),
                    errorText = {
                        Text(stringResource(Res.string.error_password_is_empty))
                    },
                    supportingText = {
                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    LocalTextStyle.current.toSpanStyle().copy(
                                        color = MaterialTheme.colorScheme.link,
                                    )
                                ) {
                                    append(stringResource(Res.string.forget_password))
                                }
                                addLink(LinkAnnotation.Url("https://imy.gdufe.edu.cn"), 0, 4)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End,
                        )
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                AnimatedVisibility(secondClassVisible) {
                    Column {
                        // 第二课堂密码
                        ElevatedPasswordTextField(
                            value = secondClassPwd,
                            onValueChange = { secondClassPwd = it },
                            modifier = Modifier.fillMaxWidth(0.9f),
                            readOnly = AppVM.isLoggingIn,
                            label = {
                                Text(stringResource(Res.string.second_class_password))
                            },
                            placeholder = {
                                Text(stringResource(Res.string.default_is_user_id))
                            },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            // 展开、折叠按钮
            item {
                IconButton(onClick = { secondClassVisible = !secondClassVisible }) {
                    Icon(
                        if (secondClassVisible) Icons.Outlined.ExpandLess else
                            Icons.Outlined.ExpandMore,
                        stringResource(
                            if (secondClassVisible) Res.string.expand_less else
                                Res.string.expand_more,
                        ),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            // 登录、退出按钮
            item {
                val isUserLogin by remember {
                    derivedStateOf { AppVM.academicSystem?.userId == userId }
                }

                ElevatedButton(
                    onClick = {
                        scope.launchCatching {
                            isLoginButtonClicked = true
                            if (isUserLogin) {
                                AppVM.logout()
                            } else {
                                showToast(getString(Res.string.logging_in))
                                AppVM.login(User(userId, password, secondClassPwd), true)
                                if (isUserLogin) {
                                    showToast(getString(Res.string.login_successful))
                                }
                            }
                        }
                    },
                    enabled = !AppVM.isLoggingIn,
                ) {
                    Text(
                        stringResource(
                            if (isUserLogin) Res.string.logout else
                                Res.string.login,
                        ),
                        color = if (isUserLogin) MaterialTheme.colorScheme.error else
                            LocalContentColor.current,
                    )
                }
            }
        }
    }
}


@Composable
private fun Logo() {
    var scaled by remember { mutableStateOf(false) }
    val size by animateDpAsState(if (scaled) 48.dp else 64.dp)

    LaunchedEffectCatching(Unit) {
        while (true) {
            delay(4000)
            repeat(2) {
                scaled = !scaled
                delay(100)
                scaled = !scaled
                delay(100)
            }
        }
    }

    Surface(
        onClick = {},
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = CircleShape,
    ) {
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painterResource(Res.drawable.punica),
                contentDescription = stringResource(Res.string.logo),
                modifier = Modifier.size(size),
            )
        }
    }
}


@Composable
private fun ElevatedPasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorText: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
) {
    var visible by remember { mutableStateOf(false) }

    ElevatedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        readOnly = readOnly,
        label = label,
        placeholder = placeholder,
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    if (visible) Icons.Outlined.Visibility else
                        Icons.Outlined.VisibilityOff,
                    stringResource(
                        if (visible) Res.string.visible else
                            Res.string.invisible,
                    ),
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else
            remember { PasswordVisualTransformation() },
        isError = isError,
        errorText = errorText,
        supportingText = supportingText,
    )
}


@Composable
private fun ElevatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean = false,
    errorText: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
) {
    Column(modifier = modifier) {
        if (label != null) {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.primary,
                    LocalTextStyle provides MaterialTheme.typography.labelSmall,
                ) {
                    label()
                }
            }
        }
        Surface(
            shape = CircleShape,
            tonalElevation = 0.5.dp,
            shadowElevation = 1.dp,
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                readOnly = readOnly,
                placeholder = placeholder?.let { { placeholder() } },
                trailingIcon = trailingIcon,
                visualTransformation = visualTransformation,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    errorBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                ),
            )
        }
        if (isError && errorText != null) {
            Box(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 8.dp)) {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.error,
                    LocalTextStyle provides MaterialTheme.typography.labelSmall,
                ) {
                    errorText()
                }
            }
        }
        if (supportingText != null) {
            Box(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 8.dp)) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.labelSmall,
                ) {
                    supportingText()
                }
            }
        }
    }
}