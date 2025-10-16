package org.kiteio.punica.mirror

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.SerializersModule
import org.kiteio.punica.mirror.storage.Preferences
import org.kiteio.punica.mirror.ui.Toast
import org.kiteio.punica.mirror.ui.ToastUiState
import org.kiteio.punica.mirror.ui.hide
import org.kiteio.punica.mirror.ui.navigation.polymorphic
import org.kiteio.punica.mirror.ui.screen.main.MainRoute
import org.kiteio.punica.mirror.ui.screen.main.mainEntry
import org.kiteio.punica.mirror.ui.screen.settings.SettingsRoute
import org.kiteio.punica.mirror.ui.screen.settings.settingsEntry
import org.kiteio.punica.mirror.ui.theme.PunicaExpressiveTheme
import org.kiteio.punica.mirror.ui.theme.ThemeMode
import org.kiteio.punica.mirror.util.syncFirst
import org.koin.compose.KoinMultiplatformApplication
import org.koin.compose.koinInject
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.Module
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.module
import org.koin.ksp.generated.module

/**
 * 小石榴 App。
 */
@OptIn(KoinExperimentalAPI::class)
@Composable
fun PunicaApp() {
    val scope = rememberCoroutineScope()

    val backStack = rememberNavBackStack(MainRoute)
    val snackbarHostState = remember { SnackbarHostState() }

    KoinMultiplatformApplication(
        config = KoinConfiguration {
            modules(
                module { single<NavBackStack<NavKey>> { backStack } },
                AppModule().module,
            )
        },
    ) {
        // 主色调
        val primaryColor by Preferences.primaryColor
            .collectAsState(Preferences.primaryColor.syncFirst())

        // 主题模式
        val themeMode by Preferences.themeMode
            .collectAsState(Preferences.themeMode.syncFirst())

        // Toast
        val toast = koinInject<Toast>()
        // Toast UI 状态
        val toastUiState by toast.uiState.collectAsStateWithLifecycle()
        var toastJob by remember { mutableStateOf<Job?>(null) }

        // 监听 Toast 状态，显示 Toast
        LaunchedEffect(toastUiState) {
            (toastUiState as? ToastUiState.Show)?.run {
                toastJob?.cancel()
                toastJob = scope.launch {
                    try {
                        launch {
                            // 展示 Snackbar
                            snackbarHostState.showSnackbar(
                                message = message,
                                withDismissAction = true,
                                // 此线程将被阻塞
                                duration = SnackbarDuration.Indefinite,
                            )
                        }

                        // 重置 Toast 状态
                        toast.hide()

                        // 延迟
                        delay(duration.timeMillis)
                    } finally {
                        // 隐藏 Snackbar
                        snackbarHostState.currentSnackbarData?.dismiss()
                    }
                }
            }
        }

        PunicaExpressiveTheme(
            darkTheme = when (themeMode) {
                ThemeMode.Auto -> isSystemInDarkTheme()
                else -> themeMode == ThemeMode.Dark
            },
            primaryColor = primaryColor,
        ) {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        snackbar = {
                            Snackbar(
                                snackbarData = it,
                                shape = MaterialTheme.shapes.medium,
                            )
                        },
                    )
                },
            ) { innerPadding ->
                NavDisplay(
                    backStack = backStack,
                    entryProvider = entryProvider {
                        mainEntry()
                        settingsEntry()
                    },
                    modifier = Modifier
                        .consumeWindowInsets(innerPadding),
                )
            }
        }
    }
}

/**
 * 返回 [NavBackStack]。
 */
@Composable
private inline fun <reified T : NavKey> rememberNavBackStack(
    vararg elements: T,
): NavBackStack<NavKey> {
    return rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(MainRoute.serializer())
                polymorphic(SettingsRoute.serializer())
            }
        },
        *elements,
    )
}

/**
 * 依赖注入模块。
 */
@Module
@ComponentScan
class AppModule