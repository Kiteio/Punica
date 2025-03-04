package org.kiteio.punica.ui.page.secondclass

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import compose.icons.CssGgIcons
import compose.icons.cssggicons.Dribbble
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.ui.component.HorizontalTabPager
import org.kiteio.punica.ui.component.NavBackAppBar
import org.kiteio.punica.ui.page.modules.ModuleRoute
import punica.composeapp.generated.resources.*

/**
 * 第二课堂路由。
 */
@Serializable
object SecondClassRoute : ModuleRoute {
    override val nameRes = Res.string.second_class
    override val icon = CssGgIcons.Dribbble
}


/**
 * 第二课堂页面。
 */
@Composable
fun SecondClassPage() = viewModel { SecondClassVM() }.Content()


@Composable
private fun SecondClassVM.Content() {
    val tabs = listOf(Res.string.grade, Res.string.activity, Res.string.log)
    val state = rememberPagerState { tabs.size }

    Scaffold(
        topBar = {
            NavBackAppBar(
                title = { Text(stringResource(SecondClassRoute.nameRes)) },
                shadowElevation = 0.dp,
            )
        }
    ) { innerPadding ->
        HorizontalTabPager(
            state = state,
            tabContent = { Text(stringResource(tabs[it])) },
            modifier = Modifier.padding(innerPadding)
        ) { page ->
            when (page) {
                0 -> Grades()
                1 -> Activities()
                2 -> GradeLogs()
            }
        }
    }
}