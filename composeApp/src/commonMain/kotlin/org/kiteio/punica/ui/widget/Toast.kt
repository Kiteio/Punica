package org.kiteio.punica.ui.widget

import NotificationType
import createNotification
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

private val notification = createNotification(
    NotificationType.TOAST
)


/**
 * 显示 [message]。
 */
fun showToast(message: String) {
    notification.show(message)
}


/**
 * 显示 [throwable]。
 */
fun showToast(throwable: Throwable) {
    notification.show(throwable.message ?: throwable.toString())
}


/**
 * 显示 [stringResource]。
 */
suspend fun showToast(stringResource: StringResource) {
    notification.show(getString(stringResource))
}