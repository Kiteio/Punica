package org.kiteio.punica.ui.page.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.foundation.Campus
import org.kiteio.punica.ui.theme.ThemeMode
import org.kiteio.punica.ui.widget.SettingsGroup
import org.kiteio.punica.ui.widget.SettingsMenuLink
import punica.composeapp.generated.resources.*

/**
 * 常用设置。
 */
@Composable
fun CommonSettingsGroup() {
    SettingsGroup(title = { Text(stringResource(Res.string.common)) }) {
        // 当前周次
        WeeKSetting()

        // 校区
        SettingsMenuLink(
            title = { Text(stringResource(Res.string.campus)) },
            subtitle = {
                Text(
                    stringResource(
                        when (AppVM.campus) {
                            Campus.CANTON -> Res.string.canton_haizhu
                            Campus.FO_SHAN -> Res.string.foshan_sanshui
                        }
                    )
                )
            },
            icon = {
                Icon(
                    Icons.Outlined.LocationOn,
                    contentDescription = stringResource(Res.string.campus),
                )
            },
            onClick = { AppVM.switchCampus() },
        )

        // 主题模式
        ThemeModeSetting()
    }
}


/**
 * 当前周次设置。
 */
@Composable
private fun WeeKSetting() {
    var expanded by remember { mutableStateOf(false) }

    Box {
        SettingsMenuLink(
            title = { Text(stringResource(Res.string.current_week)) },
            subtitle = { Text(stringResource(Res.string.week, AppVM.week)) },
            icon = {
                Icon(
                    Icons.Outlined.DateRange,
                    contentDescription = stringResource(Res.string.current_week),
                )
            },
            onClick = { expanded = true },
        )

        // 下拉菜单
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            repeat(AppVM.TIMETABLE_MAX_PAGE) {
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(Res.string.week, it),
                            color = if (it == AppVM.week) MaterialTheme.colorScheme.primary
                            else LocalContentColor.current,
                        )
                    },
                    onClick = {
                        AppVM.changeWeek(it)
                        expanded = false
                    }
                )
            }
        }
    }
}


/**
 * 主题模式设置。
 */
@Composable
private fun ThemeModeSetting() {
    var expanded by remember { mutableStateOf(false) }

    Box {
        SettingsMenuLink(
            title = { Text(stringResource(Res.string.theme_mode)) },
            subtitle = {
                Text(stringResource(AppVM.themeMode.nameRes))
            },
            icon = {
                Icon(
                    when (AppVM.themeMode) {
                        ThemeMode.Default -> Icons.Outlined.AutoMode
                        ThemeMode.Light -> Icons.Outlined.LightMode
                        ThemeMode.Dark -> Icons.Outlined.DarkMode
                    },
                    contentDescription = stringResource(Res.string.theme_mode),
                )
            },
            onClick = { expanded = true },
        )

        // 下拉菜单
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ThemeMode.entries.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(it.nameRes),
                            color = if (it == AppVM.themeMode) MaterialTheme.colorScheme.primary
                            else LocalContentColor.current,
                        )
                    },
                    onClick = {
                        AppVM.changeThemeMode(it)
                        expanded = false
                    }
                )
            }
        }
    }
}