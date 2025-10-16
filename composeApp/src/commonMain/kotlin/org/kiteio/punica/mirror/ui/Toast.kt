package org.kiteio.punica.mirror.ui

import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Toast。
 */
@Singleton
class Toast : MVI<ToastUiState, ToastIntent> {
    private val _uiState = MutableStateFlow<ToastUiState>(ToastUiState.Hide)
    override val uiState = _uiState.asStateFlow()

    override fun dispatch(intent: ToastIntent) {
        _uiState.value = when (intent) {
            is ToastIntent.Show -> ToastUiState.Show(
                intent.message,
                intent.duration,
            )
            is ToastIntent.Hide -> ToastUiState.Hide
        }
    }
}

/**
 * Toast 展示时长。
 */
sealed class ToastDuration(val timeMillis: kotlin.Long) {
    /** 短时间 */
    data object Short : ToastDuration(2400L)

    /** 长时间 */
    data object Long : ToastDuration(3000L)
}

/**
 * 显示 Toast。
 * 
 * @param message 消息
 * @param duration 展示时长
 */
fun Toast.show(
    message: String,
    duration: ToastDuration = ToastDuration.Short,
) {
    dispatch(ToastIntent.Show(message, duration))
}

/**
 * 显示异常 Toast。
 *
 * @param throwable 异常
 * @param duration 展示时长
 */
fun Toast.show(
    throwable: Throwable,
    duration: ToastDuration = ToastDuration.Short,
) {
    val message = throwable.message
        ?: throwable::class.simpleName
        ?: throwable.toString()
    dispatch(ToastIntent.Show(message, duration))
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
    /**
     * Toast 展示。
     *
     * @property message 消息
     * @property duration 展示时长
     */
    data class Show(
        val message: String,
        val duration: ToastDuration,
    ) : ToastUiState()

    /**
     * Toast 隐藏。
     */
    data object Hide : ToastUiState()
}

/**
 * Toast 意图。
 */
sealed class ToastIntent {
    /**
     * 展示 Toast。
     *
     * @property message 消息
     * @property duration 展示时长
     */
    data class Show(
        val message: String,
        val duration: ToastDuration,
    ) : ToastIntent()

    /**
     * 隐藏 Toast。
     */
    data object Hide : ToastIntent()
}