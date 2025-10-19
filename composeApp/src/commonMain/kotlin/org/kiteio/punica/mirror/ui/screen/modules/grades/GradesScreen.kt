package org.kiteio.punica.mirror.ui.screen.modules.grades

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.kiteio.punica.mirror.ui.navigation.ModuleNavKey
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.grade

/**
 * 成绩页入口。
 */
fun EntryProviderBuilder<NavKey>.gradesEntry() {
    entry<GradesRoute> { GradesScreen() }
}

/**
 * 成绩页路由。
 */
@Serializable
data object GradesRoute : ModuleNavKey {
    override val strRes = Res.string.grade
    override val icon = Icons.AutoMirrored.Outlined.ReceiptLong
}

@Composable
private fun GradesScreen() {
}