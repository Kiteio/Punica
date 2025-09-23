package org.kiteio.punica.mirror.ui.pages

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.kiteio.punica.mirror.ui.AppIntent
import org.kiteio.punica.mirror.ui.AppUiState
import org.kiteio.punica.mirror.ui.AppViewModel
import org.kiteio.punica.mirror.ui.PunicaExpressiveTheme
import org.kiteio.punica.mirror.ui.component.animation.DotLottieAnimation
import org.kiteio.punica.mirror.ui.modifier.clearFocusWhenClick
import org.koin.compose.koinInject
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.clear
import punica.composeapp.generated.resources.login
import punica.composeapp.generated.resources.password
import punica.composeapp.generated.resources.second_class_password
import punica.composeapp.generated.resources.user_id
import punica.composeapp.generated.resources.visibility
import punica.composeapp.generated.resources.visibility_off

/** 登录页面路由 */
@Serializable
object LoginRoute

/**
 * 登录页面目的地。
 */
fun NavGraphBuilder.loginDestination() {
    composable<LoginRoute> {
        val appViewModel = koinInject<AppViewModel>()
        val uiState by appViewModel.uiState.collectAsStateWithLifecycle()
        val navController = koinInject<NavHostController>()

        LoginPage(
            uiState = uiState,
            dispatch = appViewModel::dispatch,
            navController = navController,
        )
    }
}

@Preview
@Composable
private fun PreviewLoginPage() {
    val naviController = rememberNavController()

    PunicaExpressiveTheme {
        LoginPage(
            uiState = AppUiState.Init,
            dispatch = {},
            navController = naviController,
        )
    }
}

/**
 * 登录页面。
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun LoginPage(
    uiState: AppUiState,
    dispatch: (AppIntent) -> Unit,
    navController: NavHostController,
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()

    Scaffold(
        modifier = Modifier.clearFocusWhenClick(),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Outlined.ArrowBackIosNew,
                            contentDescription = "",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .consumeWindowInsets(innerPadding),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (
                adaptiveInfo.windowSizeClass
                    .isWidthAtLeastBreakpoint(
                        WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND,
                    )
            ) {
                Box(
                    modifier = Modifier.fillMaxHeight().weight(.5f),
                    contentAlignment = Alignment.Center,
                ) {
                    DotLottieAnimation(
                        "files/tourists_by_car.lottie",
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            val user = if (uiState is AppUiState.LoggedIn) {
                uiState.user
            } else null

            MainPane(
                userId = rememberTextFieldState(user?.id ?: ""),
                password = rememberTextFieldState(user?.password ?: ""),
                secondClassPwd = rememberTextFieldState(user?.secondClassPwd ?: ""),
                modifier = Modifier.fillMaxHeight().weight(.5f),
                onLogin = { userId, password, secondClassPwd ->
                    dispatch(
                        AppIntent.Login(userId, password, secondClassPwd)
                    )
                }
            )
        }
    }
}

/**
 * 主窗格。
 *
 * @param userId 学号
 * @param password 密码
 * @param secondClassPwd 第二课堂密码
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalHazeMaterialsApi::class)
@Composable
private fun MainPane(
    userId: TextFieldState,
    password: TextFieldState,
    secondClassPwd: TextFieldState,
    onLogin: (
        userId: String,
        password: String,
        secondClassPwd: String,
    ) -> Unit,
    modifier: Modifier = Modifier,
) {
    val userIdInteractionSource = remember { MutableInteractionSource() }

    var passwordVisible by remember { mutableStateOf(false) }
    val passwordInteractionSource = remember { MutableInteractionSource() }

    var secondClassPwdVisible by remember { mutableStateOf(false) }
    val secondClassPwdInteractionSource = remember { MutableInteractionSource() }

    val colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surface.copy(.3f),
        unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(.3f),
        errorContainerColor = MaterialTheme.colorScheme.surface.copy(.3f),
        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.BottomCenter,
        ) {
            DotLottieAnimation(
                "files/cat_is_sleeping_and_rolling.lottie",
                contentDescription = null,
                reverseOnRepeat = true,
                modifier = Modifier.widthIn(max = 80.dp)
                    .scale(2.5f),
            )
        }
        OutlinedTextField(
            userId,
            modifier = Modifier.width(280.dp),
            label = {
                Text(stringResource(Res.string.user_id))
            },
            trailingIcon = {
                ClearIconButton(
                    userId,
                    interactionSource = userIdInteractionSource,
                )
            },
            supportingText = if (with(userId.text) { isNotEmpty() && length < 11 }) {
                { Text("${userId.text.length}/11") }
            } else {
                null
            },
            isError = userId.text.isNotEmpty() && userId.text.length < 11,
            inputTransformation = {
                if (!asCharSequence().all { it.isDigit() } || length > 11) {
                    revertAllChanges()
                }
            },
            lineLimits = TextFieldLineLimits.SingleLine,
            colors = colors,
            interactionSource = userIdInteractionSource,
        )
        Spacer(Modifier.height(8.dp))
        OutlinedSecureTextField(
            password,
            modifier = Modifier.width(280.dp),
            label = {
                Text(stringResource(Res.string.password))
            },
            trailingIcon = {
                Row {
                    ClearIconButton(
                        password,
                        interactionSource = passwordInteractionSource,
                    )
                    VisibilityIconButton(
                        passwordVisible,
                        onClick = { passwordVisible = !passwordVisible },
                    )
                }
            },
            textObfuscationMode = with(TextObfuscationMode) {
                if (passwordVisible) Visible else RevealLastTyped
            },
            colors = colors,
            interactionSource = passwordInteractionSource,
        )
        Spacer(Modifier.height(8.dp))
        OutlinedSecureTextField(
            secondClassPwd,
            modifier = Modifier.width(280.dp),
            label = {
                Text(stringResource(Res.string.second_class_password))
            },
            trailingIcon = {
                Row {
                    ClearIconButton(
                        secondClassPwd,
                        interactionSource = secondClassPwdInteractionSource,
                    )
                    VisibilityIconButton(
                        secondClassPwdVisible,
                        onClick = { secondClassPwdVisible = !secondClassPwdVisible },
                    )
                }
            },
            textObfuscationMode = with(TextObfuscationMode) {
                if (secondClassPwdVisible) Visible else RevealLastTyped
            },
            colors = colors,
            interactionSource = secondClassPwdInteractionSource,
        )
        Spacer(Modifier.fillMaxHeight(.1f))
        Button(
            onClick = {
                onLogin(
                    userId.text.toString(),
                    password.text.toString(),
                    secondClassPwd.text.toString(),
                )
            },
        ) {
            Text(stringResource(Res.string.login))
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun ClearIconButton(
    state: TextFieldState,
    interactionSource: MutableInteractionSource,
) {
    val isFocus by interactionSource.collectIsFocusedAsState()

    if (state.text.isNotBlank() && isFocus) {
        IconButton(
            onClick = state::clearText,
            // 延迟 isFocus 变为 false，确保文本能清除
            interactionSource = interactionSource,
        ) {
            Icon(
                Icons.Outlined.Close,
                contentDescription = stringResource(Res.string.clear),
            )
        }
    }
}

@Composable
private fun VisibilityIconButton(
    visible: Boolean,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
    ) {
        Icon(
            with(Icons.Outlined) {
                if (visible) Visibility else VisibilityOff
            },
            contentDescription = stringResource(
                with(Res.string) {
                    if (visible) visibility else visibility_off
                },
            ),
        )
    }
}