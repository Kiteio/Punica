package org.kiteio.punica.mirror

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
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
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(MainRoute.serializer())
                polymorphic(SettingsRoute.serializer())
            }
        },
        MainRoute,
    )

    KoinMultiplatformApplication(
        config = KoinConfiguration {
            modules(
                module { single<NavBackStack<NavKey>> { backStack } },
                AppModule().module,
            )
        },
    ) {
        val toast = koinInject<Toast>()
        val toastUiState by toast.uiState.collectAsStateWithLifecycle()

        // 监听 Toast 状态，显示 Toast
        LaunchedEffect(toastUiState) {
            (toastUiState as? ToastUiState.Show)?.run {
                // TODO: ShowToast。
                toast.hide()
            }
        }

        // 主色调
        val primaryColor by Preferences.primaryColor
            .collectAsState(Preferences.primaryColor.syncFirst())

        val themeMode by Preferences.themeMode
            .collectAsState(Preferences.themeMode.syncFirst())

        PunicaExpressiveTheme(
            darkTheme = when (themeMode) {
                ThemeMode.Auto -> isSystemInDarkTheme()
                else -> themeMode == ThemeMode.Dark
            },
            primaryColor = primaryColor,
        ) {
            NavDisplay(
                backStack = backStack,
                entryProvider = entryProvider {
                    mainEntry()
                    settingsEntry()
                },
            )
        }
    }
}

/**
 * 依赖注入模块。
 */
@Module
@ComponentScan
class AppModule