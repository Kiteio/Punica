package org.kiteio.punica.wrapper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.kiteio.punica.ui.widget.showToast

/**
 * [launch] try ... catch。
 */
fun CoroutineScope.launchCatching(block: suspend CoroutineScope.() -> Unit) {
    launch {
        try {
            block()
        } catch (throwable: Throwable) {
            handleException(throwable)
        }
    }
}


/**
 * [LaunchedEffect] try ... catch。
 */
@Composable
fun LaunchedEffectCatching(vararg keys: Any?, block: suspend CoroutineScope.() -> Unit) {
    LaunchedEffect(keys) {
        try {
            block()
        } catch (throwable: Throwable) {
            handleException(throwable)
        }
    }
}


private fun handleException(throwable: Throwable) {
    // 过滤无关异常
    if (
        throwable.message !in listOf(
            "rememberCoroutineScope left the composition",
            "The coroutine scope left the composition",
            "Mutation interrupted",
            "Job was cancelled"
        )
    ) showToast(throwable)
}