package org.kiteio.punica.mirror.ui.screen.modules.websites

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.kiteio.punica.mirror.ui.navigation.ModuleNavKey
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.guang_cai_websites

/**
 * 广财网站页入口。
 */
fun EntryProviderScope<NavKey>.websitesEntry() {
    entry<WebsitesRoute> { WebsitesScreen() }
}

/**
 * 广财网站页路由。
 */
@Serializable
data object WebsitesRoute : ModuleNavKey {
    override val strRes = Res.string.guang_cai_websites
    override val icon = Icons.Outlined.Apps
}

@Composable
private fun WebsitesScreen() {
}