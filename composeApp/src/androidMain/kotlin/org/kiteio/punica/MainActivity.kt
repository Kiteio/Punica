package org.kiteio.punica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat
import org.kiteio.punica.mirror.PunicaApp
import org.kiteio.punica.mirror.storage.Preferences
import org.kiteio.punica.mirror.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val view = LocalView.current
            val themeMode by Preferences.themeMode
                .collectAsState(ThemeMode.Default)
            val isSystemInDarkTheme = isSystemInDarkTheme()

            LaunchedEffect(themeMode, isSystemInDarkTheme) {
                val isLightTheme = when (themeMode) {
                    ThemeMode.Auto -> !isSystemInDarkTheme
                    else -> themeMode == ThemeMode.Light
                }

                WindowInsetsControllerCompat(window, view).apply {
                    // 状态栏跟随主题模式变化
                    isAppearanceLightStatusBars = isLightTheme
                    // 导航栏跟随主题模式变化
                    isAppearanceLightNavigationBars = isLightTheme
                }
            }

            PunicaApp()
        }
    }
}