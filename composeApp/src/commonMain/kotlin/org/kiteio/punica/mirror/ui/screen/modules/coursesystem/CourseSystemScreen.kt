package org.kiteio.punica.mirror.ui.screen.modules.coursesystem

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import compose.icons.CssGgIcons
import compose.icons.cssggicons.Slack
import kotlinx.serialization.Serializable
import org.kiteio.punica.mirror.ui.navigation.ModuleNavKey
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.course_system

/**
 * 选课系统页入口。
 */
fun EntryProviderScope<NavKey>.courseSystemEntry() {
    entry<CourseSystemRoute> { CourseSystemScreen() }
}

/**
 * 选课系统页路由。
 */
@Serializable
data object CourseSystemRoute : ModuleNavKey {
    override val strRes = Res.string.course_system
    override val icon = CssGgIcons.Slack
}

@Composable
private fun CourseSystemScreen() {
}