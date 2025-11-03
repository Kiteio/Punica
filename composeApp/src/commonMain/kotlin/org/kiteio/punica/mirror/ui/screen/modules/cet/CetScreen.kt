package org.kiteio.punica.mirror.ui.screen.modules.cet

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.kiteio.punica.mirror.ui.navigation.ModuleNavKey
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.cet

/**
 * 四六级页入口。
 */
fun EntryProviderScope<NavKey>.cetEntry() {
    entry<CetRoute> { CetScreen() }
}

/**
 * 四六级页路由。
 */
@Serializable
data object CetRoute : ModuleNavKey {
    override val strRes = Res.string.cet
    override val icon = Icons.Outlined.Verified
}

@Composable
private fun CetScreen() {
}