package org.kiteio.punica.mirror.ui.screen.modules.plans

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import compose.icons.TablerIcons
import compose.icons.tablericons.Rocket
import kotlinx.serialization.Serializable
import org.kiteio.punica.mirror.ui.navigation.ModuleNavKey
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.implementation_plan

/**
 * 执行计划页入口。
 */
fun EntryProviderBuilder<NavKey>.plansEntry() {
    entry<PlansRoute> { PlansScreen() }
}

/**
 * 执行计划页路由。
 */
@Serializable
data object PlansRoute : ModuleNavKey {
    override val strRes = Res.string.implementation_plan
    override val icon = TablerIcons.Rocket
}

@Composable
private fun PlansScreen() {
}