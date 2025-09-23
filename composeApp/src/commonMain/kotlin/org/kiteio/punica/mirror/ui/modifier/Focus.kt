package org.kiteio.punica.mirror.ui.modifier

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager

/**
 * 点击时清除焦点。
 */
fun Modifier.clearFocusWhenClick(): Modifier {
    return composed {
        val focusManager = LocalFocusManager.current

        pointerInput(Unit) {
            detectTapGestures {
                focusManager.clearFocus()
            }
        }
    }
}