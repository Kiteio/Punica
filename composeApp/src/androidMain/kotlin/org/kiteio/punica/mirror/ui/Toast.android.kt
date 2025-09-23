package org.kiteio.punica.mirror.ui

import NotificationType
import createNotification

private val notification = createNotification(
    NotificationType.TOAST
)

actual fun showToast(message: String) {
    notification.show(message)
}