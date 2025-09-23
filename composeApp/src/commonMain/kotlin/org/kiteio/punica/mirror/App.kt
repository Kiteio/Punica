package org.kiteio.punica.mirror

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import org.kiteio.punica.mirror.platform.Platform
import org.kiteio.punica.mirror.ui.PunicaExpressiveTheme
import org.kiteio.punica.mirror.ui.Toast
import org.kiteio.punica.mirror.ui.ToastUiState
import org.kiteio.punica.mirror.ui.appModule
import org.kiteio.punica.mirror.ui.hide
import org.kiteio.punica.mirror.ui.pages.NavigationRoute
import org.kiteio.punica.mirror.ui.pages.loginDestination
import org.kiteio.punica.mirror.ui.pages.navigation.settingModule
import org.kiteio.punica.mirror.ui.pages.navigationDestination
import org.kiteio.punica.mirror.ui.showToast
import org.kiteio.punica.mirror.ui.toastModule
import org.koin.compose.KoinMultiplatformApplication
import org.koin.compose.koinInject
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.module

@OptIn(KoinExperimentalAPI::class)
@Composable
fun App(
    platform: Platform,
    snackbarHostState: SnackbarHostState? = null,
) {
    val isDarkTheme = false
    val navController = rememberNavController()

    KoinMultiplatformApplication(
        config = KoinConfiguration {
            modules(
                module { single { platform } },
                module { single { navController } },
                appModule,
                toastModule,
                settingModule,
            )
        },
    ) {
        val toast = koinInject<Toast>()
        val toastUiState by toast.uiState.collectAsStateWithLifecycle()

        // 监听 Toast 状态，显示 Toast
        LaunchedEffect(toastUiState) {
            (toastUiState as? ToastUiState.Show)?.run {
                showToast(message)
                toast.hide()
            }
        }

        PunicaExpressiveTheme(
            isDarkTheme = isDarkTheme,
            modifyColorScheme = if (!isDarkTheme)
                platform::whiteBackgroundOnDesktop else null,
        ) {
            Scaffold(
                snackbarHost = {
                    if (snackbarHostState != null) {
                        SnackbarHost(snackbarHostState)
                    }
                },
            ) {
                NavHost(
                    navController = navController,
                    startDestination = NavigationRoute,
                ) {
                    navigationDestination()
                    loginDestination()
                }
            }
        }
    }
}

/**
 * 当 [Platform] 为 [Platform.Desktop] 时，
 * 将 [ColorScheme] 的背景色设置为白色。
 */
private fun Platform.whiteBackgroundOnDesktop(
    colorScheme: ColorScheme,
): ColorScheme {
    return if (this == Platform.Desktop) {
        colorScheme.copy(
            background = Color.White,
            surface = Color.White,
        )
    } else {
        colorScheme
    }
}