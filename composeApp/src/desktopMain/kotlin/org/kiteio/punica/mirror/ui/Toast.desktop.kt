package org.kiteio.punica.mirror.ui

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

private val scope = MainScope()
internal val snackbarHostState = SnackbarHostState()

actual fun showToast(message: String) {
    scope.launch {
        snackbarHostState.showSnackbar(
            message,
            withDismissAction = true,
        )
    }
}