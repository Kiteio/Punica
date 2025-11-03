package org.kiteio.punica.mirror.ui.screen.modules.teachers

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import compose.icons.TablerIcons
import compose.icons.tablericons.Id
import kotlinx.serialization.Serializable
import org.kiteio.punica.mirror.ui.navigation.ModuleNavKey
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.teacher_profile

/**
 * 教师信息入口。
 */
fun EntryProviderScope<NavKey>.teachersScreen() {
    entry<TeachersRoute> { TeachersScreen() }
}

/**
 * 教师信息路由。
 */
@Serializable
data object TeachersRoute : ModuleNavKey {
    override val strRes = Res.string.teacher_profile
    override val icon = TablerIcons.Id
}

@Composable
private fun TeachersScreen() {
}