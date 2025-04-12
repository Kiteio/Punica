package org.kiteio.punica.ui.page.versions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.Build
import org.kiteio.punica.client.gitee.Gitee
import org.kiteio.punica.client.gitee.api.Release
import org.kiteio.punica.client.gitee.api.getReleases
import org.kiteio.punica.ui.component.CardListItem
import org.kiteio.punica.ui.component.LoadingNotNullOrEmpty
import org.kiteio.punica.ui.component.NavBackAppBar
import org.kiteio.punica.wrapper.format
import org.kiteio.punica.wrapper.launchCatching
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.current_version
import punica.composeapp.generated.resources.old_edition
import punica.composeapp.generated.resources.version

/**
 * 版本页面路由。
 */
@Serializable
object VersionsRoute {
    val nameRes = Res.string.version
    val icon = Icons.Outlined.Verified
}


/**
 * 版本页面。
 */
@Composable
fun VersionsPage() = Content()


@Composable
private fun Content() {
    var isLoading by remember { mutableStateOf(true) }
    val releases by produceState<List<Release>?>(null) {
        launchCatching {
            try {
                val gitee = Gitee()
                value = gitee.getReleases("Kiteio", "Punica-CMP") +
                        gitee.getReleases("Kiteio", "Punica")
            } finally {
                isLoading = false
            }
        }
    }

    var releaseBottomSheetVisible by remember { mutableStateOf(false) }
    var release by remember { mutableStateOf<Release?>(null) }

    Scaffold(
        topBar = {
            NavBackAppBar(title = { Text(stringResource(VersionsRoute.nameRes)) })
        },
    ) { innerPadding ->
        LoadingNotNullOrEmpty(
            releases,
            isLoading = isLoading,
            modifier = Modifier.padding(innerPadding),
        ) { releases ->
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(232.dp),
                contentPadding = PaddingValues(8.dp),
            ) {
                items(releases) {
                    Release(
                        it,
                        onClick = {
                            release = it
                            releaseBottomSheetVisible = true
                        },
                        modifier = Modifier.padding(8.dp),
                    )
                }
            }
        }
    }

    ReleaseBottomSheet(
        releaseBottomSheetVisible,
        onDismissRequest = {
            releaseBottomSheetVisible = false
            release = null
        },
        release = release,
    )
}


@Composable
private fun Release(release: Release, onClick: () -> Unit, modifier: Modifier = Modifier) {
    CardListItem(
        headlineContent = {
            Text(
                buildString {
                    append(release.name)
                    // 当前版本
                    if (release.name == Build.versionName) append(" ${stringResource(Res.string.current_version)}")
                }
            )
        },
        onClick = onClick,
        modifier = modifier,
        supportingContent = {
            Column {
                Spacer(modifier = Modifier.height(4.dp))
                // 创建时间
                Text(release.time.format(), style = MaterialTheme.typography.bodySmall)
            }
        },
        // 旧版标记
        trailingContent = if (release.name.first() < '1') {
            {
                Text(stringResource(Res.string.old_edition))
            }
        } else null,
    )
}