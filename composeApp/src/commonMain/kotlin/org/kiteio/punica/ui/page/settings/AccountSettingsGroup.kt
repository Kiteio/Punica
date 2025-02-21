package org.kiteio.punica.ui.page.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import compose.icons.CssGgIcons
import compose.icons.TablerIcons
import compose.icons.cssggicons.Dribbble
import compose.icons.tablericons.Wifi
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.ui.widget.SettingsGroup
import org.kiteio.punica.ui.widget.SettingsMenuLink
import punica.composeapp.generated.resources.*

/**
 * 账号设置。
 */
@Composable
fun AccountSettingsGroup() {
    SettingsGroup(title = { Text(stringResource(Res.string.account)) }) {
        // 教务系统
        SettingsMenuLink(
            title = { Text(stringResource(Res.string.academic_system)) },
            subtitle = {
                Text(
                    AppVM.user?.id?.toString()
                        ?: stringResource(Res.string.not_logged_in),
                )
            },
            icon = {
                Icon(
                    Icons.Outlined.School,
                    contentDescription = stringResource(Res.string.academic_system),
                )
            },
            onClick = {},
        )

        // 第二课堂
        SettingsMenuLink(
            title = { Text(stringResource(Res.string.second_class)) },
            subtitle = {
                Text(
                    AppVM.user?.id?.toString()
                        ?: stringResource(Res.string.not_logged_in),
                )
            },
            icon = {
                Icon(
                    CssGgIcons.Dribbble,
                    contentDescription = stringResource(Res.string.second_class),
                )
            },
            onClick = {},
        )

        // 校园网
        SettingsMenuLink(
            title = { Text(stringResource(Res.string.campus_network)) },
            subtitle = {
                Text(
                    AppVM.user?.id?.toString()
                        ?: stringResource(Res.string.not_logged_in),
                )
            },
            icon = {
                Icon(
                    TablerIcons.Wifi,
                    contentDescription = stringResource(Res.string.campus_network),
                )
            },
            onClick = {},
        )
    }
}