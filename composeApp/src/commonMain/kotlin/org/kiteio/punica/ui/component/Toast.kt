package org.kiteio.punica.ui.component

/**
 * 显示 [message]。
 */
expect fun showToast(message: String)


/**
 * 显示 [throwable]。
 */
fun showToast(throwable: Throwable) {
    showToast(throwable.message ?: throwable::class.simpleName!!)
}