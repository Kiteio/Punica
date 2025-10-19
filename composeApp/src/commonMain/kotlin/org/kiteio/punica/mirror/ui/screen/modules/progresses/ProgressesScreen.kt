package org.kiteio.punica.mirror.ui.screen.modules.progresses

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import compose.icons.TablerIcons
import compose.icons.tablericons.ChartLine
import kotlinx.serialization.Serializable
import org.kiteio.punica.mirror.ui.navigation.ModuleNavKey
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.academic_progress

/**
 * 学业进度页入口。
 */
fun EntryProviderBuilder<NavKey>.progressesEntry() {
    entry<ProgressesRoute> { ProgressesScreen() }
}

/**
 * 学业进度页路由。
 */
@Serializable
data object ProgressesRoute : ModuleNavKey {
    override val strRes = Res.string.academic_progress
    override val icon = TablerIcons.ChartLine
}

@Composable
private fun ProgressesScreen() {
}