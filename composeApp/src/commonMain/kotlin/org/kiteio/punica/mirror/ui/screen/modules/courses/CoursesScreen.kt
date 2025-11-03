package org.kiteio.punica.mirror.ui.screen.modules.courses

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import compose.icons.TablerIcons
import compose.icons.tablericons.Book
import kotlinx.serialization.Serializable
import org.kiteio.punica.mirror.ui.navigation.ModuleNavKey
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.course_timetable

/**
 * 课程课表页入口。
 */
fun EntryProviderScope<NavKey>.coursesEntry() {
    entry<CoursesRoute> { CoursesScreen() }
}

/**
 * 课程课表页路由。
 */
@Serializable
data object CoursesRoute : ModuleNavKey {
    override val strRes = Res.string.course_timetable
    override val icon = TablerIcons.Book
}

@Composable
private fun CoursesScreen() {
}