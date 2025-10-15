package org.kiteio.punica.mirror.ui.screen.modules

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMosaic
import androidx.compose.material.icons.outlined.AutoAwesomeMosaic
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.kiteio.punica.mirror.ui.navigation.BottomNavKey
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.modules

/**
 * 模块页入口。
 */
fun EntryProviderBuilder<NavKey>.modulesEntry() {
    entry<ModulesRoute> { ModulesScreen() }
}

/**
 * 模块页路由。
 */
@Serializable
data object ModulesRoute : BottomNavKey {
    override val strRes = Res.string.modules
    override val icon = Icons.Outlined.AutoAwesomeMosaic
    override val selectedIcon = Icons.Filled.AutoAwesomeMosaic
}

@Composable
private fun ModulesScreen() {

}