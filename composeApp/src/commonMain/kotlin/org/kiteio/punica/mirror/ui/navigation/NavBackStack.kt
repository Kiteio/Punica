package org.kiteio.punica.mirror.ui.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * 返回上一个页面，若堆栈中只有一个路由，则不进行任何操作。
 */
fun NavBackStack<NavKey>.navigateUp() {
    if (size != 1) {
        removeLast()
    }
}

/**
 * 将堆栈清空，重置为 [route]。
 */
fun NavBackStack<NavKey>.resetTo(route: NavKey) {
    clear()
    add(route)
}