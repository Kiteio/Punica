package org.kiteio.punica.mirror.ui.animation

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import io.github.alexzhirkevich.compottie.*
import punica.composeapp.generated.resources.Res

/**
 * lottie 资源 [Painter]。
 *
 * @param isPlaying 是否正在播放
 * @param restartOnPlay 如果 [isPlaying] 变为 true，会决定是否重置进度和迭代
 * @param reverseOnRepeat 在重复播放时是否倒放
 * @param clipSpec 裁切描述
 * @param speed 播放速率
 * @param iterations 迭代次数
 * @param cancellationBehavior 动画取消行为
 * @param useCompositionFrameRate 是否使用动画声明的帧率
 */
@Composable
fun dotLottiePainterResource(
    path: String,
    isPlaying: Boolean = true,
    restartOnPlay: Boolean = true,
    reverseOnRepeat: Boolean = false,
    clipSpec: LottieClipSpec? = null,
    speed: Float = 1f,
    iterations: Int = Compottie.IterateForever,
    cancellationBehavior: LottieCancellationBehavior = LottieCancellationBehavior.Immediately,
    useCompositionFrameRate: Boolean = false,
): Painter {
    val composition by rememberLottieComposition {
        LottieCompositionSpec.DotLottie(
            Res.readBytes(path),
        )
    }
    var mIsPlaying by remember { mutableStateOf(true) }

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        mIsPlaying = true
    }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        mIsPlaying = false
    }

    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = isPlaying && mIsPlaying,
        restartOnPlay = restartOnPlay,
        reverseOnRepeat = reverseOnRepeat,
        clipSpec = clipSpec,
        speed = speed,
        iterations = iterations,
        cancellationBehavior = cancellationBehavior,
        useCompositionFrameRate = useCompositionFrameRate,
    )

    return rememberLottiePainter(composition, { progress })
}