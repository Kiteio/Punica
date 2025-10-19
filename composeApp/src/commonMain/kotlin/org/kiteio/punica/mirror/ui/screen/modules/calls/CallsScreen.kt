package org.kiteio.punica.mirror.ui.screen.modules.calls

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.kiteio.punica.mirror.ui.navigation.ModuleNavKey
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.emergency_call

/**
 * 紧急电话页入口。
 */
fun EntryProviderBuilder<NavKey>.callsEntry() {
    entry<CallsRoute> { CallsScreen() }
}

/**
 * 紧急电话页路由。
 */
@Serializable
data object CallsRoute : ModuleNavKey {
    override val strRes = Res.string.emergency_call
    override val icon = Icons.Outlined.Call
}

@Composable
private fun CallsScreen() {
}