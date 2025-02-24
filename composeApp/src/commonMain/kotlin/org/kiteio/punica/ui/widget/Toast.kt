package org.kiteio.punica.ui.widget

import NotificationType
import createNotification

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
    notification.show(throwable.message ?: throwable::class.simpleName!!)
}