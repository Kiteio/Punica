package org.kiteio.punica.ui.page.modules

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMosaic
import androidx.compose.material.icons.outlined.AutoAwesomeMosaic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.ui.component.PunicaCard
import org.kiteio.punica.ui.component.PunicaListItem
import org.kiteio.punica.ui.compositionlocal.LocalNavController
import org.kiteio.punica.ui.compositionlocal.LocalWindowSizeClass
import org.kiteio.punica.ui.compositionlocal.isCompactWidth
import org.kiteio.punica.ui.page.call.EmergencyCallRoute
import org.kiteio.punica.ui.page.cet.CETRoute
import org.kiteio.punica.ui.page.course.CourseSystemRoute
import org.kiteio.punica.ui.page.exam.ExamRoute
import org.kiteio.punica.ui.page.grades.GradesRoute
import org.kiteio.punica.ui.page.home.TopLevelRoute
import org.kiteio.punica.ui.page.notice.NoticeRoute
import org.kiteio.punica.ui.page.plan.PlanRoute
import org.kiteio.punica.ui.page.progress.ProgressRoute
import org.kiteio.punica.ui.page.secondclass.SecondClassRoute
import org.kiteio.punica.ui.page.teacher.TeacherProfileRoute
import org.kiteio.punica.ui.page.timetables.CourseTimetableRoute
import org.kiteio.punica.ui.page.websites.WebsitesRoute
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.modules

/**
 * 模块页面路由。
 */
@Serializable
object ModulesRoute : TopLevelRoute {
    override val nameRes = Res.string.modules
    override val icon = Icons.Outlined.AutoAwesomeMosaic
    override val toggledIcon = Icons.Filled.AutoAwesomeMosaic
}


/**
 * 模块页面。
 */
@Composable
fun ModulesPage() = Content()


@Composable
private fun Content() {
    val windowSizeClass = LocalWindowSizeClass.current
    val isCompactWidth = windowSizeClass.isCompactWidth
    val navController = LocalNavController.current

    val modules = listOf(
        EmergencyCallRoute,
        NoticeRoute,
        WebsitesRoute,
        CourseSystemRoute,
        ExamRoute,
        CETRoute,
        GradesRoute,
        SecondClassRoute,
        TeacherProfileRoute,
        CourseTimetableRoute,
        PlanRoute,
        ProgressRoute,
    )

    Scaffold(contentWindowInsets = WindowInsets.statusBars) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(if (isCompactWidth) 160.dp else 120.dp),
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            items(modules, key = { it.nameRes.key }) {
                Module(
                    route = it,
                    onClick = { navController.navigate(it) },
                    isCompactWidth = isCompactWidth,
                    modifier = Modifier.padding(8.dp),
                )
            }
        }
    }
}


/**
 * 模块。
 *
 * @param route 模块路由
 */
@Composable
private fun Module(
    route: ModuleRoute,
    onClick: () -> Unit,
    isCompactWidth: Boolean,
    modifier: Modifier = Modifier,
) {
    PunicaCard(onClick = onClick, modifier = modifier) {
        val name = stringResource(route.nameRes)

        if (isCompactWidth) {
            PunicaListItem(
                headlineContent = {
                    Text(name, color = MaterialTheme.colorScheme.primary)
                },
                leadingContent = {
                    Surface(
                        onClick = onClick,
                        tonalElevation = 1.5.dp,
                        shadowElevation = 1.5.dp,
                        shape = CircleShape,
                        modifier = Modifier.size(28.dp),
                    ) {
                        Icon(
                            route.icon,
                            contentDescription = name,
                            modifier = Modifier.padding(4.dp),
                        )
                    }
                },
            )
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                Icon(route.icon, contentDescription = name)
                Spacer(modifier = Modifier.height(12.dp))
                Text(name, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}


/**
 * 模块路由。
 *
 * @property nameRes 名称字符串资源
 * @property icon 图标
 */
interface ModuleRoute {
    val nameRes: StringResource
    val icon: ImageVector
}