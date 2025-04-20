package org.kiteio.punica.ui.page.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AutoMode
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Web
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Github
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.Build
import org.kiteio.punica.client.academic.foundation.Campus
import org.kiteio.punica.ui.component.showToast
import org.kiteio.punica.ui.compositionlocal.LocalNavController
import org.kiteio.punica.ui.page.home.TopLevelRoute
import org.kiteio.punica.ui.page.versions.VersionsRoute
import org.kiteio.punica.ui.theme.ThemeMode
import org.kiteio.punica.wrapper.launchCatching
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.app_name
import punica.composeapp.generated.resources.campus
import punica.composeapp.generated.resources.canton_haizhu
import punica.composeapp.generated.resources.current_week
import punica.composeapp.generated.resources.foshan_sanshui
import punica.composeapp.generated.resources.github
import punica.composeapp.generated.resources.logging_in
import punica.composeapp.generated.resources.not_logged_in
import punica.composeapp.generated.resources.official_website
import punica.composeapp.generated.resources.official_website_kiteio
import punica.composeapp.generated.resources.open_source_repository
import punica.composeapp.generated.resources.poem
import punica.composeapp.generated.resources.punica
import punica.composeapp.generated.resources.settings
import punica.composeapp.generated.resources.theme_mode
import punica.composeapp.generated.resources.week_of

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


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Content() {
    val scope = rememberCoroutineScope()
    val userId by AppVM.userIdFlow.collectAsState(null)
    var userBottomSheetVisible by remember { mutableStateOf(false) }

    Scaffold(contentWindowInsets = WindowInsets.statusBars) {
        Box(modifier = Modifier) {
            // 背景
            Image(
                painterResource(Res.drawable.punica),
                contentDescription = stringResource(Res.string.app_name),
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f).blur(4.dp),
                contentScale = ContentScale.Crop,
            )
            // 蒙版
            Box(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f)
                    .background(LocalContentColor.current.copy(0.1f))
            )
            Column(modifier = Modifier.padding(8.dp).offset(y = (-48).dp)) {
                Spacer(modifier = Modifier.fillMaxHeight(0.3f))
                // 账号
                Surface(
                    onClick = { userBottomSheetVisible = true },
                    shape = CircleShape,
                    contentColor = MaterialTheme.colorScheme.primary,
                    tonalElevation = 0.5.dp,
                    shadowElevation = 1.dp,
                    modifier = Modifier.padding(8.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Logo(
                            onClick = {
                                if (AppVM.academicSystem == null) {
                                    scope.launchCatching {
                                        showToast(getString(Res.string.logging_in))
                                        AppVM.login()
                                    }
                                }
                            },
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            // 学号
                            Text(
                                userId ?: stringResource(Res.string.not_logged_in),
                                fontWeight = FontWeight.Bold,
                                color = if (AppVM.academicSystem != null) MaterialTheme.colorScheme.primary else
                                    MaterialTheme.colorScheme.error,
                            )

                            // 诗句
                            Text(
                                stringResource(Res.string.poem),
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }
                // 设置、信息
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                ) {
                    item { ThemeModeSetting() }
                    item { WeekSetting() }
                    item { CampusSetting() }
                    item { Versions() }
                    item { PunicaGitHub() }
                    item { OfficialWebsite() }
                }
            }
        }
    }

    UserBottomSheet(
        userBottomSheetVisible,
        onDismissRequest = { userBottomSheetVisible = false },
    )
}


@Composable
private fun Logo(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        shadowElevation = 2.dp,
    ) {
        Image(
            painterResource(Res.drawable.punica),
            contentDescription = stringResource(Res.string.app_name),
            modifier = Modifier.size(64.dp).padding(8.dp),
        )
    }
}


@Composable
private fun ThemeModeSetting() {
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    val themeMode by AppVM.themeModeFlow.collectAsState(ThemeMode.Default)

    Box {
        Setting(
            onClick = { expanded = true },
            leadingIcon = {
                Icon(
                    when (themeMode) {
                        ThemeMode.Default -> Icons.Outlined.AutoMode
                        ThemeMode.Light -> Icons.Outlined.LightMode
                        ThemeMode.Dark -> Icons.Outlined.DarkMode
                    },
                    contentDescription = stringResource(Res.string.theme_mode),
                )
            },
            title = { Text(stringResource(Res.string.theme_mode)) },
            supportText = {
                Text(stringResource(themeMode.nameRes))
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp),
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


@Composable
private fun WeekSetting() {
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    val week by AppVM.weekFlow.collectAsState(1)

    Box {
        Setting(
            onClick = { expanded = true },
            leadingIcon = {
                Icon(
                    Icons.Outlined.DateRange,
                    contentDescription = stringResource(Res.string.current_week),
                )
            },
            title = { Text(stringResource(Res.string.current_week)) },
            supportText = {
                Text(stringResource(Res.string.week_of, week))
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp),
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


@Composable
private fun CampusSetting() {
    val scope = rememberCoroutineScope()

    // 校区
    val campus by AppVM.campusFlow.collectAsState(Campus.CANTON)
    Setting(
        onClick = { scope.launchCatching { AppVM.switchCampus() } },
        leadingIcon = {
            Icon(
                Icons.Outlined.DateRange,
                contentDescription = stringResource(Res.string.current_week),
            )
        },
        title = { Text(stringResource(Res.string.campus)) },
        supportText = {
            Text(
                stringResource(
                    when (campus) {
                        Campus.CANTON -> Res.string.canton_haizhu
                        Campus.FO_SHAN -> Res.string.foshan_sanshui
                    }
                )
            )
        },
        modifier = Modifier.fillMaxWidth().padding(8.dp),
    )
}


@Composable
private fun Versions() {
    val navController = LocalNavController.current

    Setting(
        onClick = { navController.navigate(VersionsRoute) },
        leadingIcon = {
            Icon(
                VersionsRoute.icon,
                contentDescription = stringResource(VersionsRoute.nameRes),
            )
        },
        title = { Text(stringResource(VersionsRoute.nameRes)) },
        supportText = {
            Text(Build.versionName)
        },
        modifier = Modifier.fillMaxWidth().padding(8.dp),
    )
}


@Composable
private fun PunicaGitHub() {
    val uriHandler = LocalUriHandler.current

    Setting(
        onClick = { uriHandler.openUri(Build.punicaGitHub) },
        leadingIcon = {
            Icon(
                SimpleIcons.Github,
                contentDescription = stringResource(Res.string.github),
            )
        },
        title = { Text(stringResource(Res.string.github)) },
        supportText = {
            Text(stringResource(Res.string.open_source_repository))
        },
        modifier = Modifier.fillMaxWidth().padding(8.dp),
    )
}


@Composable
private fun OfficialWebsite() {
    val uriHandler = LocalUriHandler.current

    Setting(
        onClick = { uriHandler.openUri(Build.officialWebsite) },
        leadingIcon = {
            Icon(
                Icons.Outlined.Web,
                contentDescription = stringResource(Res.string.official_website),
            )
        },
        title = { Text(stringResource(Res.string.official_website)) },
        supportText = {
            Text(stringResource(Res.string.official_website_kiteio))
        },
        modifier = Modifier.fillMaxWidth().padding(8.dp),
    )
}


@Composable
private fun Setting(
    onClick: () -> Unit,
    leadingIcon: @Composable () -> Unit,
    title: @Composable () -> Unit,
    supportText: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 0.5.dp,
        shadowElevation = 1.dp,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(20.dp)) {
                    leadingIcon()
                }
                Spacer(modifier = Modifier.width(8.dp))
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                    content = title,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.bodySmall,
                LocalContentColor provides LocalContentColor.current.copy(0.6f),
            ) {
                supportText()
            }
        }
    }
}