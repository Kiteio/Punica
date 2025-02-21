package org.kiteio.punica.ui.page.modules

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMosaic
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import compose.icons.CssGgIcons
import compose.icons.TablerIcons
import compose.icons.cssggicons.*
import compose.icons.tablericons.*
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.ui.compositionlocal.LocalNavController
import org.kiteio.punica.ui.compositionlocal.LocalWindowSizeClass
import org.kiteio.punica.ui.compositionlocal.isCompactWidth
import org.kiteio.punica.ui.page.home.TopLevelRoute
import punica.composeapp.generated.resources.*

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
        ModuleImpl(Res.string.emergency_call, Icons.Outlined.Call),
        ModuleImpl(Res.string.academic_notice, CssGgIcons.Bell),
        ModuleImpl(Res.string.frequently_used_websites, Icons.Outlined.StarOutline),
        ModuleImpl(Res.string.course_system, CssGgIcons.Slack),
        ModuleImpl(Res.string.exam, CssGgIcons.Time),
        ModuleImpl(Res.string.cet, Icons.Outlined.Verified),
        ModuleImpl(Res.string.grade, CssGgIcons.Clipboard),
        ModuleImpl(Res.string.second_class, CssGgIcons.Dribbble),
        ModuleImpl(Res.string.teacher_profile, TablerIcons.Id),
        ModuleImpl(Res.string.course_timetable, TablerIcons.Book),
        ModuleImpl(Res.string.implementation_plan, TablerIcons.Rocket),
        ModuleImpl(Res.string.academic_progress, TablerIcons.ChartLine),
        ModuleImpl(Res.string.campus_network, TablerIcons.Wifi),
        ModuleImpl(Res.string.otp, Icons.Outlined.VerifiedUser),
    )

    Scaffold(contentWindowInsets = WindowInsets.statusBars) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(if (isCompactWidth) 160.dp else 120.dp),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(innerPadding),
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
    ElevatedCard(onClick = onClick, modifier = modifier) {
        val name = stringResource(route.nameRes)

        if (isCompactWidth) {
            ListItem(
                headlineContent = { Text(name) },
                leadingContent = { Icon(route.icon, contentDescription = name) },
            )
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                Icon(route.icon, contentDescription = name)
                Spacer(modifier = Modifier.height(12.dp))
                Text(name)
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


private data class ModuleImpl(
    override val nameRes: StringResource,
    override val icon: ImageVector,
) : ModuleRoute