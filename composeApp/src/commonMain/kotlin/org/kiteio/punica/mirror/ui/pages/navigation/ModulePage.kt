package org.kiteio.punica.mirror.ui.pages.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

/**
 * 模块页面路由。
 */
@Serializable
data object ModuleRoute

/**
 * 模块页面目的地。
 */
fun NavGraphBuilder.moduleDestination() {
    composable<ModuleRoute> {

    }
}