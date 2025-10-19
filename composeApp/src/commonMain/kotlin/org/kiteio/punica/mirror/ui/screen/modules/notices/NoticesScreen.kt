package org.kiteio.punica.mirror.ui.screen.modules.notices

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import compose.icons.CssGgIcons
import compose.icons.cssggicons.Bell
import kotlinx.serialization.Serializable
import org.kiteio.punica.mirror.ui.navigation.ModuleNavKey
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.academic_notices

/**
 * 教务通知页入口。
 */
fun EntryProviderBuilder<NavKey>.noticesEntry() {
    entry<NoticesRoute> { NoticesScreen() }
}

/**
 * 教务通知页路由。
 */
@Serializable
data object NoticesRoute : ModuleNavKey {
    override val strRes = Res.string.academic_notices
    override val icon = CssGgIcons.Bell
}

@Composable
private fun NoticesScreen() {
}