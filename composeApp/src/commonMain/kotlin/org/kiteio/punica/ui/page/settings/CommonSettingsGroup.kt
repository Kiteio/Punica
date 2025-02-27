package org.kiteio.punica.ui.page.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.foundation.Campus
import org.kiteio.punica.ui.theme.ThemeMode
import org.kiteio.punica.ui.widget.SettingsGroup
import org.kiteio.punica.ui.widget.SettingsMenuLink
import org.kiteio.punica.wrapper.launchCatching
import punica.composeapp.generated.resources.*

/**
 * 常用设置。
 */
@Composable
fun CommonSettingsGroup() {
    val scope = rememberCoroutineScope()

    SettingsGroup(title = { Text(stringResource(Res.string.common)) }) {
        // 主题模式
        ThemeModeSetting(scope = scope)

        // 当前周次
        WeekSetting(scope = scope)

        // 校区
        val campus by AppVM.campus.collectAsState(Campus.CANTON)
        SettingsMenuLink(
            title = { Text(stringResource(Res.string.campus)) },
            subtitle = {
                Text(
                    stringResource(
                        when (campus) {
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
            onClick = { scope.launchCatching { AppVM.switchCampus() } },
        )
    }
}


/**
 * 主题模式设置。
 */
@Composable
private fun ThemeModeSetting(scope: CoroutineScope) {
    var expanded by remember { mutableStateOf(false) }
    val themeMode by AppVM.themeMode.collectAsState(ThemeMode.Default)

    Box {
        SettingsMenuLink(
            title = { Text(stringResource(Res.string.theme_mode)) },
            subtitle = {
                Text(stringResource(themeMode.nameRes))
            },
            icon = {
                Icon(
                    when (themeMode) {
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
                            color = if (it == themeMode) MaterialTheme.colorScheme.primary
                            else LocalContentColor.current,
                        )
                    },
                    onClick = {
                        scope.launchCatching {
                            AppVM.changeThemeMode(it)
                            expanded = false
                        }
                    }
                )
            }
        }
    }
}


/**
 * 当前周次设置。
 */
@Composable
private fun WeekSetting(scope: CoroutineScope) {
    var expanded by remember { mutableStateOf(false) }
    val week by AppVM.week.collectAsState(1)

    Box {
        SettingsMenuLink(
            title = { Text(stringResource(Res.string.current_week)) },
            subtitle = { Text(stringResource(Res.string.week_of, week)) },
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
                            stringResource(Res.string.week_of, it),
                            color = if (it == week) MaterialTheme.colorScheme.primary
                            else LocalContentColor.current,
                        )
                    },
                    onClick = {
                        scope.launchCatching {
                            AppVM.changeWeek(it)
                            expanded = false
                        }
                    }
                )
            }
        }
    }
}