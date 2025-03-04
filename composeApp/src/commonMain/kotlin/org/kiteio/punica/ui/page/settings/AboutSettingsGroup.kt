package org.kiteio.punica.ui.page.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.buildAnnotatedString
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Github
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.Build
import org.kiteio.punica.ui.component.SettingsGroup
import org.kiteio.punica.ui.component.SettingsMenuLink
import org.kiteio.punica.ui.component.showToast
import org.kiteio.punica.ui.compositionlocal.LocalNavController
import org.kiteio.punica.ui.page.versions.VersionsRoute
import org.kiteio.punica.wrapper.launchCatching
import punica.composeapp.generated.resources.*

/**
 * 关于设置。
 */
@Composable
fun AboutSettingsGroup() {
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current

    val githubUrl = "https://github.com/Kiteio/Punica-CMP"
    val littleRedBookId = "95634885169"
    val littleRedBookUserName = "@Kiteio"

    SettingsGroup(title = { Text(stringResource(Res.string.about)) }) {
        // 版本
        SettingsMenuLink(
            title = { Text(stringResource(VersionsRoute.nameRes)) },
            subtitle = { Text(Build.versionName) },
            icon = {
                Icon(
                    VersionsRoute.icon,
                    contentDescription = stringResource(VersionsRoute.nameRes),
                )
            },
            onClick = { navController.navigate(VersionsRoute) },
        )
        // GitHub
        SettingsMenuLink(
            title = { Text(stringResource(Res.string.github)) },
            subtitle = { Text(githubUrl.replace("https://", "")) },
            icon = {
                Icon(
                    SimpleIcons.Github,
                    contentDescription = stringResource(Res.string.github),
                )
            },
            onClick = { uriHandler.openUri(githubUrl) },
        )
        // 小红书
        SettingsMenuLink(
            title = { Text(stringResource(Res.string.little_red_book)) },
            subtitle = { Text(littleRedBookUserName) },
            icon = {
                Icon(
                    Icons.Outlined.AlternateEmail,
                    contentDescription = stringResource(Res.string.little_red_book),
                )
            },
            onClick = {
                scope.launchCatching {
                    val text = buildAnnotatedString { append(littleRedBookId) }
                    clipboardManager.setText(text)
                    showToast(getString(Res.string.copy_successful))
                }
            },
        )
    }
}