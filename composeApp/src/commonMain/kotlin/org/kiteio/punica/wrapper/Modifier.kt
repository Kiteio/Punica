package org.kiteio.punica.wrapper

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager

/**
 * 清除焦点。
 */
fun Modifier.focusCleaner(focusManager: FocusManager) =
    clickable(
        interactionSource = null,
        indication = null
    ) { focusManager.clearFocus() }