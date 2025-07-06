package org.kiteio.punica.mirror.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 支持 [MaterialExpressiveTheme] 的 Punica 主题。
 *
 * @param isDarkTheme 是否为暗色主题
 * @param modifyColorScheme 修改颜色方案
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PunicaExpressiveTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    modifyColorScheme: ((ColorScheme) -> ColorScheme)? = null,
    content: @Composable () -> Unit,
) {
    val colorScheme = rememberDynamicColorScheme(
        primary = Color(0xff5b89e8),
        isAmoled = false,
        isDark = isDarkTheme,
        style = PaletteStyle.Expressive,
        modifyColorScheme = modifyColorScheme,
    )

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        motionScheme = MotionScheme.expressive(),
        content = content,
    )
}

@Preview
@Composable
fun PreviewPunicaExpressiveTheme() {
    PunicaExpressiveTheme {
        Scaffold { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Hello, World!")
                Button(onClick = {}) {
                    Text("Button")
                }
            }
        }
    }
}