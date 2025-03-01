package org.kiteio.punica.ui.widget

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.kiteio.punica.wrapper.launchCatching

private val scope = CoroutineScope(SupervisorJob())

val snackbarHostState = SnackbarHostState()

actual fun showToast(message: String) {
    scope.launchCatching {
        snackbarHostState.showSnackbar(
            message,
            withDismissAction = true,
        )
    }
}