package org.kiteio.punica.mirror.ui.screen.modules.secondclass

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import compose.icons.CssGgIcons
import compose.icons.cssggicons.Dribbble
import kotlinx.serialization.Serializable
import org.kiteio.punica.mirror.ui.navigation.ModuleNavKey
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.second_class

/**
 * 第二课堂页入口。
 */
fun EntryProviderScope<NavKey>.secondClassEntry() {
    entry<SecondClassRoute> { SecondClassScreen() }
}

/**
 * 第二课堂页路由。
 */
@Serializable
data object SecondClassRoute: ModuleNavKey {
    override val strRes = Res.string.second_class
    override val icon = CssGgIcons.Dribbble
}

@Composable
private fun SecondClassScreen() {
}