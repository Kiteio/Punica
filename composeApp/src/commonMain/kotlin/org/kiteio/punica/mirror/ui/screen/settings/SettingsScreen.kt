package org.kiteio.punica.mirror.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material.icons.outlined.Web
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Github
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.Build
import org.kiteio.punica.mirror.ui.component.NavBeforeIconButton
import org.kiteio.punica.mirror.ui.theme.ThemeMode
import org.koin.compose.viewmodel.koinViewModel
import punica.composeapp.generated.resources.*

/**
 * 设置页入口。
 */
fun EntryProviderScope<NavKey>.settingsEntry() {
    entry<SettingsRoute> { SettingsScreen() }
}

/**
 * 设置页路由。
 */
@Serializable
data object SettingsRoute : NavKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen() {
    val viewModel = koinViewModel<SettingsViewModel>()
    val themeMode by viewModel.themeMode.collectAsState(ThemeMode.Default)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.settings))
                },
                navigationIcon = {
                    NavBeforeIconButton()
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                // 版本卡片
                VersionCard(
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item {
                // 主题模式设置
                ThemeModeSetting(
                    themeMode = themeMode,
                    onThemeModeChange = viewModel::changeThemeMode,
                )
            }
        }
    }
}

/**
 * 版本卡片。
 */
@Composable
private fun VersionCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.extraSmall,
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    stringResource(Res.string.version),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    Build.versionName,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Row {
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Outlined.Update,
                        contentDescription = stringResource(Res.string.official_website),
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        SimpleIcons.Github,
                        contentDescription = stringResource(Res.string.github),
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Outlined.Web,
                        contentDescription = stringResource(Res.string.official_website),
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Outlined.Share,
                        contentDescription = stringResource(Res.string.official_website),
                    )
                }
            }

            Text(
                stringResource(Res.string.power_by_compose_multiplatform),
                color = LocalContentColor.current.copy(0.6f),
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

/**
 * 主题模式设置。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeModeSetting(
    themeMode: ThemeMode,
    onThemeModeChange: (themeMode: ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingCard(modifier = modifier) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            ThemeMode.entries.forEachIndexed { index, item ->
                SegmentedButton(
                    selected = themeMode == item,
                    onClick = { onThemeModeChange(item) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = ThemeMode.entries.size,
                    ),
                    label = { Text(stringResource(item.strRes)) },
                )
            }
        }
    }
}

/**
 * 设置卡片。
 */
@Composable
private fun SettingCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 2.dp,
        tonalElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SettingTitle(stringResource(Res.string.theme_mode))
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

/**
 * 设置标题。
 */
@Composable
private fun SettingTitle(text: String) {
    Text(
        text,
        fontWeight = FontWeight.Black,
        style = MaterialTheme.typography.labelMedium,
    )
}