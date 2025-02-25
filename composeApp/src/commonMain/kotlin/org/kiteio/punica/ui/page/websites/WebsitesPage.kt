package org.kiteio.punica.ui.page.websites

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import compose.icons.CssGgIcons
import compose.icons.TablerIcons
import compose.icons.cssggicons.Dribbble
import compose.icons.tablericons.Wifi
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.ui.page.modules.ModuleRoute
import org.kiteio.punica.ui.widget.NavBackAppBar
import punica.composeapp.generated.resources.*

/**
 * 常用网站路由。
 */
@Serializable
object WebsitesRoute : ModuleRoute {
    override val nameRes = Res.string.frequently_used_websites
    override val icon = Icons.Outlined.StarOutline
}


/**
 * 常用网站页面。
 */
@Composable
fun WebsitesPage() = Content()


@Composable
private fun Content() {
    Scaffold(
        topBar = { NavBackAppBar(title = { Text(stringResource(WebsitesRoute.nameRes)) }) }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(200.dp),
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(4.dp),
        ) {
            websites()
        }
    }
}


/**
 * 网站。
 */
private fun LazyGridScope.websites() {
    item {
        // 融合门户
        WebSite(
            name = stringResource(Res.string.gdufe_portal),
            icon = Icons.Outlined.Portrait,
            urlString = "https://imy.gdufe.edu.cn",
        )
    }
    item {
        // 一体化平台
        WebSite(
            name = stringResource(Res.string.gdufe_all),
            icon = Icons.Outlined.Ballot,
            urlString = "https://sec.gdufe.edu.cn",
        )
    }
    item {
        // 广财通
        WebSite(
            name = stringResource(Res.string.gdufe_app),
            icon = Icons.Outlined.AppSettingsAlt,
            urlString = "https://gctzy.gdufe.edu.cn",
        )
    }
    item {
        // 教务系统
        WebSite(
            name = stringResource(Res.string.academic_system),
            icon = Icons.Outlined.School,
            urlString = "http://jwxt.gdufe.edu.cn/jsxsd",
        )
    }
    item {
        // 第二课堂
        WebSite(
            name = stringResource(Res.string.second_class),
            icon = CssGgIcons.Dribbble,
            urlString = "http://2ketang.gdufe.edu.cn",
        )
    }
    item {
        // 校园网
        WebSite(
            name = stringResource(Res.string.campus_network),
            icon = TablerIcons.Wifi,
            urlString = "http://100.64.13.17",
        )
    }
    item {
        // 财务系统
        WebSite(
            name = stringResource(Res.string.gdufe_finance),
            icon = Icons.Outlined.AttachMoney,
            urlString = "https://cwsfxt.gdufe.edu.cn",
        )
    }
    item {
        // 广财慕课
        WebSite(
            name = stringResource(Res.string.gdufe_mooc),
            icon = Icons.Outlined.OndemandVideo,
            urlString = "https://www.gdufemooc.cn",
        )
    }
    item {
        // 毕博平台
        WebSite(
            name = stringResource(Res.string.gdufe_blackboard),
            icon = Icons.Outlined.SpaceDashboard,
            urlString = "https://bb.gdufe.edu.cn",
        )
    }
    item {
        // 图书馆
        WebSite(
            name = stringResource(Res.string.library),
            icon = Icons.Outlined.LocalLibrary,
            urlString = "https://lib.gdufe.edu.cn",
        )
    }
}


/**
 * 网站。
 */
@Composable
private fun WebSite(name: String, icon: ImageVector, urlString: String) {
    val uriHandler = LocalUriHandler.current

    ElevatedCard(onClick = { uriHandler.openUri(urlString) }, modifier = Modifier.padding(4.dp)) {
        ListItem(
            headlineContent = { Text(name) },
            supportingContent = {
                Text(
                    urlString.replace(Regex("^https://|^http://"), ""),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            leadingContent = { Icon(icon, contentDescription = name) },
        )
    }
}