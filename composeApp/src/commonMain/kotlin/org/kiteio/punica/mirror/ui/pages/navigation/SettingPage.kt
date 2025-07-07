package org.kiteio.punica.mirror.ui.pages.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

/**
 * 设置页面路由。
 */
@Serializable
data object SettingRoute

/**
 * 设置页面目的地。
 */
fun NavGraphBuilder.settingDestination() {
    composable<SettingRoute> {

    }
}