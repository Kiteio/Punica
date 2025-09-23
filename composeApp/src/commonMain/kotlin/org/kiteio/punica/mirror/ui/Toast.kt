package org.kiteio.punica.mirror.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Toast。
 */
expect fun showToast(message: String)

/** Toast Koin 模块 */
val toastModule = module {
    singleOf(::Toast)
}

/**
 * Toast。
 */
class Toast() : MVI<ToastUiState, ToastIntent> {
    private val _uiState = MutableStateFlow<ToastUiState>(ToastUiState.Hide)
    override val uiState = _uiState.asStateFlow()

    override fun dispatch(intent: ToastIntent) {
        _uiState.value = when (intent) {
            is ToastIntent.Show -> ToastUiState.Show(intent.message)
            is ToastIntent.Hide -> ToastUiState.Hide
        }
    }
}

/**
 * 显示 Toast。
 */
fun Toast.show(message: String) {
    dispatch(ToastIntent.Show(message))
}

/**
 * 显示异常 Toast。
 */
fun Toast.show(throwable: Throwable) {
    val message = throwable.message
        ?: throwable::class.simpleName
        ?: throwable.toString()
    dispatch(ToastIntent.Show(message))
}

/**
 * 隐藏 Toast。
 */
fun Toast.hide() {
    dispatch(ToastIntent.Hide)
}

/**
 * Toast 状态。
 */
sealed class ToastUiState {
    data class Show(val message: String) : ToastUiState()

    data object Hide : ToastUiState()
}

/**
 * Toast 意图。
 */
sealed class ToastIntent {
    data class Show(val message: String) : ToastIntent()

    data object Hide : ToastIntent()
}