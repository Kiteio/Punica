package org.kiteio.punica.wrapper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.ktor.client.plugins.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.kiteio.punica.ui.widget.showToast
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.connect_timeout

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


private suspend fun handleException(throwable: Throwable) {
    // 过滤无关异常
    if (
        throwable.message !in listOf(
            "rememberCoroutineScope left the composition",
            "The coroutine scope left the composition",
            "Mutation interrupted",
            "Job was cancelled"
        )
    ) {
        when (throwable) {
            is HttpRequestTimeoutException -> {
                showToast(getString(Res.string.connect_timeout))
            }

            else -> {
                showToast(throwable)
            }
        }
    }
}