package org.kiteio.punica.mirror.ui.screen.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.window.core.layout.WindowSizeClass
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.mirror.modal.User
import org.kiteio.punica.mirror.storage.AppDatabase
import org.kiteio.punica.mirror.ui.AppViewModel
import org.kiteio.punica.mirror.ui.animation.dotLottiePainterResource
import org.kiteio.punica.mirror.ui.bottomsheet.LoginHelpBottomSheet
import org.kiteio.punica.mirror.ui.component.NavBeforeIconButton
import org.kiteio.punica.mirror.ui.component.VisibilityIconButton
import org.kiteio.punica.mirror.ui.modifier.focusCleaner
import org.kiteio.punica.mirror.ui.modifier.pressToScale
import org.koin.compose.koinInject
import punica.composeapp.generated.resources.*

/**
 * 登录页入口。
 */
fun EntryProviderScope<NavKey>.loginEntry() {
    entry<LoginRoute> { LoginScreen() }
}

/**
 * 登录页路由。
 */
@Serializable
data object LoginRoute : NavKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginScreen() {
    val appViewModel = koinInject<AppViewModel>()
    val database = koinInject<AppDatabase>()

    val adaptiveInfo = currentWindowAdaptiveInfo()

    val userState by appViewModel.userState.collectAsState()
    val user by remember {
        flow {
            userState.userId?.let { userId ->
                emit(database.userDao().getById(userId))
            }
        }
    }.collectAsState(null)

    val isWidthAtLeastMediumLowerBound = adaptiveInfo.windowSizeClass
        .isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND,
        )

    var loginHelpBottomSheetVisible by remember { mutableStateOf(false) }



    Scaffold(
        topBar = {
            TopAppBar(
                onHelp = {
                    loginHelpBottomSheetVisible = true
                },
            )
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .focusCleaner(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedVisibility(
                isWidthAtLeastMediumLowerBound,
                modifier = Modifier.weight(1f),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // 欢迎
                    WelcomeText()
                    // 旅行车
                    Image(
                        dotLottiePainterResource(
                            "files/animation/tourists_by_car.lottie",
                        ),
                        contentDescription = stringResource(Res.string.travel),
                    )
                }
            }

            // 登录表单
            LoginForm(
                initialUser = user,
                welcomeTextVisible = !isWidthAtLeastMediumLowerBound,
                onLogin = { userId, password, secondClassPwd ->
                    appViewModel.login(
                        userId = userId,
                        password = password,
                        secondClassPwd = secondClassPwd,
                    )
                },
                modifier = Modifier.weight(1f),
            )
        }
    }

    // 登录帮助底部抽屉
    LoginHelpBottomSheet(
        visible = loginHelpBottomSheetVisible,
        onDismissRequest = { loginHelpBottomSheetVisible = false },
    )
}

/**
 * 顶部导航栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    onHelp: () -> Unit,
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            NavBeforeIconButton()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
        ),
        actions = {
            val help = stringResource(Res.string.help)
            AppBarRow {
                // 帮助按钮
                clickableItem(
                    onClick = onHelp,
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Outlined.Help,
                            contentDescription = help,
                        )
                    },
                    label = help,
                )
            }
        }
    )
}

/**
 * 欢迎。
 */
@Composable
private fun WelcomeText(modifier: Modifier = Modifier) {
    Text(
        stringResource(Res.string.welcome),
        modifier = modifier,
        fontWeight = FontWeight.Black,
        style = MaterialTheme.typography.headlineMedium,
    )
}

/**
 * 登录表单。
 *
 * @param initialUser 初始化用户
 * @param welcomeTextVisible 欢迎文字是否可见
 */
@Composable
private fun LoginForm(
    initialUser: User?,
    welcomeTextVisible: Boolean,
    onLogin: (userId: String, password: String, secondClassPwd: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    // 学号
    val userId = rememberTextFieldState()
    // 密码
    val password = rememberTextFieldState()
    // 第二课堂密码
    val secondClassPwd = rememberTextFieldState()

    // 登录按钮是否已点击
    var isLoginButtonClicked by remember { mutableStateOf(false) }

    // 用户名是否错误（长度不为 11）
    val isUserIdError by remember {
        derivedStateOf {
            (isLoginButtonClicked || userId.text.isNotEmpty())
                    && userId.text.length < 11
        }
    }
    // 密码是否错误（长度不为 0）
    val isPasswordError by remember {
        derivedStateOf { isLoginButtonClicked && password.text.isEmpty() }
    }

    LaunchedEffect(initialUser) {
        // 初始化用户
        initialUser?.let { user ->
            userId.setTextAndPlaceCursorAtEnd(user.id)
            password.setTextAndPlaceCursorAtEnd(user.password)
            secondClassPwd.setTextAndPlaceCursorAtEnd(user.secondClassPwd)
        }
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(16.dp),
    ) {
        item {
            if (welcomeTextVisible) {
                // 欢迎
                WelcomeText(modifier = Modifier.animateItem())
            }
            Spacer(Modifier.height(8.dp))
            // 猫猫
            Image(
                dotLottiePainterResource(
                    "files/animation/cat_is_sleeping_and_rolling.lottie",
                    reverseOnRepeat = true,
                ),
                contentDescription = stringResource(Res.string.cat),
                modifier = Modifier.size(120.dp).scale(1.6f).pressToScale(),
            )

            // 学号
            OutlinedTextField(
                userId,
                modifier = Modifier.fillMaxWidth(0.8f),
                label = {
                    Text(stringResource(Res.string.user_id))
                },
                placeholder = {
                    Text(stringResource(Res.string.tip_input_user_id))
                },
                supportingText = (@Composable {
                    Text(stringResource(Res.string.error_user_id_is_11_digits))
                }).takeIf { isUserIdError },
                isError = isUserIdError,
                inputTransformation = InputTransformation.maxLength(11)
                    .then {
                        // 只能输入数字
                        if (!asCharSequence().all { it.isDigit() }) {
                            revertAllChanges()
                        }
                    },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                onKeyboardAction = { focusManager.moveFocus(FocusDirection.Down) },
                lineLimits = TextFieldLineLimits.SingleLine,
            )
        }
        item {
            // 密码
            OutlinedSecureTextField(
                password,
                modifier = Modifier.fillMaxWidth(0.8f),
                label = {
                    Text(stringResource(Res.string.password))
                },
                placeholder = {
                    Text(stringResource(Res.string.tip_input_password))
                },
                supportingText = (@Composable {
                    Text(stringResource(Res.string.error_password_is_empty))
                }).takeIf { isPasswordError },
                isError = isPasswordError,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                onKeyboardAction = { focusManager.moveFocus(FocusDirection.Down) },
            )
        }
        item {
            // 第二课堂密码
            OutlinedSecureTextField(
                secondClassPwd,
                modifier = Modifier.fillMaxWidth(0.8f),
                label = {
                    Text(stringResource(Res.string.second_class_password))
                },
                placeholder = {
                    Text(stringResource(Res.string.tip_input_second_class_pwd))
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                onKeyboardAction = { focusManager.clearFocus() },
            )
        }
        item {
            Spacer(Modifier.height(8.dp))
            // 登录按钮
            Button(
                onClick = {
                    isLoginButtonClicked = true
                    onLogin(
                        "${userId.text}",
                        "${password.text}",
                        "${secondClassPwd.text}",
                    )
                },
                modifier = Modifier.fillMaxWidth(0.8f),
            ) {
                Text(stringResource(Res.string.login))
            }
        }
    }
}

/**
 * 密码输入框。
 */
@Composable
private fun OutlinedSecureTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    label: @Composable (TextFieldLabelScope.() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        autoCorrectEnabled = false,
        keyboardType = KeyboardType.Password,
    ),
    onKeyboardAction: KeyboardActionHandler? = null,
) {
    var visible by remember { mutableStateOf(false) }

    OutlinedSecureTextField(
        state,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        trailingIcon = {
            VisibilityIconButton(
                visible = visible,
                onVisibleChange = { visible = it },
            )
        },
        supportingText = supportingText,
        isError = isError,
        textObfuscationMode = if (visible) {
            TextObfuscationMode.Visible
        } else {
            TextObfuscationMode.RevealLastTyped
        },
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
    )
}