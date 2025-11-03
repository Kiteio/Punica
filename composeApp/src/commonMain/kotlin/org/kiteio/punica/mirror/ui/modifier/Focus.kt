package org.kiteio.punica.mirror.ui.modifier

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalFocusManager

/**
 * 点击时清除焦点。
 */
fun Modifier.focusCleaner(): Modifier {
    return composed {
        val focusManager = LocalFocusManager.current

        clickable(
            interactionSource = null,
            indication = null,
        ) {
            focusManager.clearFocus()
        }
    }
}