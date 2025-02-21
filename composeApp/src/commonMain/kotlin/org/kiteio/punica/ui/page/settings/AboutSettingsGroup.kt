package org.kiteio.punica.ui.page.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.Build
import org.kiteio.punica.ui.widget.SettingsGroup
import org.kiteio.punica.ui.widget.SettingsMenuLink
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.about
import punica.composeapp.generated.resources.version

/**
 * 关于设置。
 */
@Composable
fun AboutSettingsGroup() {
    SettingsGroup(title = { Text(stringResource(Res.string.about)) }) {
        // 版本
        SettingsMenuLink(
            title = { Text(stringResource(Res.string.version)) },
            subtitle = { Text(Build.versionName) },
            icon = {
                Icon(
                    Icons.Outlined.Verified,
                    contentDescription = stringResource(Res.string.version),
                )
            },
            onClick = {},
        )
    }
}