package org.kiteio.punica.mirror.ui.component.animation

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.DotLottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import punica.composeapp.generated.resources.Res

/**
 * dotLottie 动画。
 *
 * @param path dotLottie 资源路径
 * @param contentDescription
 * @param reverseOnRepeat 是否在重复时反向播放
 */
@Composable
fun DotLottieAnimation(
    path: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    reverseOnRepeat: Boolean = false,
) {
    val composition by rememberLottieComposition {
        LottieCompositionSpec.DotLottie(
            Res.readBytes(path)
        )
    }
    var isPlaying by remember { mutableStateOf(true) }
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = isPlaying,
        reverseOnRepeat = reverseOnRepeat,
        iterations = Compottie.IterateForever,
    )

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        isPlaying = true
    }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        isPlaying = false
    }

    Image(
        rememberLottiePainter(composition, { progress }),
        contentDescription = contentDescription,
        modifier = modifier,
    )
}