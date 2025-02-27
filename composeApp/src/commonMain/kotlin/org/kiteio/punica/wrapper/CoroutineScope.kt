package org.kiteio.punica.wrapper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kiteio.punica.ui.widget.showToast

/**
 * [launch] try ... catch。
 */
fun CoroutineScope.launchCatching(block: suspend CoroutineScope.() -> Unit) {
    launch {
        withContext(Dispatchers.Default) {
            try {
                block()
            } catch (e: Throwable) {
                handleException(e)
            }
        }
    }
}


/**
 * [LaunchedEffect] try ... catch。
 */
@Composable
fun LaunchedEffectCatching(vararg keys: Any?, block: suspend CoroutineScope.() -> Unit) {
    LaunchedEffect(keys) {
        withContext(Dispatchers.Default) {
            try {
                block()
            } catch (e: Throwable) {
                handleException(e)
            }
        }
    }
}


private fun handleException(e: Throwable) {
    // 过滤协程被取消
    if (
        e.message != "rememberCoroutineScope left the composition" &&
        e.message != "The coroutine scope left the composition"
    ) showToast(e)
}