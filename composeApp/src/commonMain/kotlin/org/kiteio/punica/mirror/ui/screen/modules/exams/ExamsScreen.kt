package org.kiteio.punica.mirror.ui.screen.modules.exams

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.kiteio.punica.mirror.ui.navigation.ModuleNavKey
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.exam

/**
 * 考试安排页入口。
 */
fun EntryProviderBuilder<NavKey>.examsEntry() {
    entry<ExamsRoute> { ExamsScreen() }
}

/**
 * 考试安排页路由。
 */
@Serializable
data object ExamsRoute: ModuleNavKey {
    override val strRes = Res.string.exam
    override val icon = Icons.AutoMirrored.Outlined.Assignment
}

@Composable
private fun ExamsScreen() {
}