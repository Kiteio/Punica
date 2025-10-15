package org.kiteio.punica.mirror.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.DynamicMaterialExpressiveTheme

/**
 * 小石榴主题。
 *
 * @param darkTheme 是否为暗色
 * @param primaryColor 主色调
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PunicaExpressiveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    primaryColor: Color = MaterialTheme.punicaColor,
    content: @Composable () -> Unit,
) {
    DynamicMaterialExpressiveTheme(
        primary = primaryColor,
        motionScheme = MotionScheme.expressive(),
        isDark = darkTheme,
        animate = true,
        content = content,
    )
}

/** 小石榴默认主色调 */
val MaterialTheme.punicaColor
    get() = Color(0xFF0F52BA)