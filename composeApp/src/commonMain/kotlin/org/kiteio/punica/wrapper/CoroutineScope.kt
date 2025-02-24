package org.kiteio.punica.wrapper

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.kiteio.punica.ui.widget.showToast

/**
 * [launch] try ... catch ...。
 */
fun CoroutineScope.launchCatching(block: suspend CoroutineScope.() -> Unit) {
    launch {
        try {
            block()
        } catch (e: Exception) {
            showToast(e)
        }
    }
}