package org.kiteio.punica.ui.page.settings

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable
import org.kiteio.punica.ui.page.home.TopLevelRoute
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.settings

/**
 * 设置页面路由。
 */
@Serializable
object SettingsRoute : TopLevelRoute {
    override val nameRes = Res.string.settings
    override val icon = Icons.Outlined.Settings
    override val toggledIcon = Icons.Filled.Settings
}


/**
 * 设置页面。
 */
@Composable
fun SettingsPage() = Content()


@Composable
private fun Content() {
    Scaffold(contentWindowInsets = WindowInsets.statusBars) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            item { AccountSettingsGroup() }
            item { CommonSettingsGroup() }
            item { AboutSettingsGroup() }
        }
    }
}