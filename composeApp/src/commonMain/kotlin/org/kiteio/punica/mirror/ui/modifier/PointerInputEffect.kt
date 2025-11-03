package org.kiteio.punica.mirror.ui.modifier

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput

/**
 * 按下缩放。
 */
fun Modifier.pressToScale(
    targetScale: Float = 0.9f,
    onClick: (() -> Unit)? = null,
) = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (isPressed) targetScale else 1f,
    )

    scale(scale).pointerInput(Unit) {
        detectTapGestures(
            onPress = {
                isPressed = true
                try {
                    awaitRelease()
                } finally {
                    isPressed = false
                }
            },
            onTap = onClick?.let { { onClick() } },
        )
    }
}