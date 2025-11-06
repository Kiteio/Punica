package org.kiteio.punica.mirror.ui.screen.modules.calls

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.twotone.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.mirror.modal.Call
import org.kiteio.punica.mirror.modal.education.Campus
import org.kiteio.punica.mirror.platform.ClipEntry
import org.kiteio.punica.mirror.ui.Toast
import org.kiteio.punica.mirror.ui.component.NavBeforeIconButton
import org.kiteio.punica.mirror.ui.navigation.ModuleNavKey
import org.kiteio.punica.mirror.ui.show
import org.koin.compose.koinInject
import punica.composeapp.generated.resources.*

/**
 * 紧急电话页入口。
 */
fun EntryProviderScope<NavKey>.callsEntry() {
    entry<CallsRoute> { CallsScreen() }
}

/**
 * 紧急电话页路由。
 */
@Serializable
data object CallsRoute : ModuleNavKey {
    override val strRes = Res.string.emergency_call
    override val icon = Icons.Outlined.Call
}

@Composable
private fun CallsScreen() {
    val pagerState = rememberPagerState { Campus.entries.size }

    Scaffold(
        topBar = { TopAppBar() },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // 校区标签行
            CampusTabRow(pagerState = pagerState)
            // 电话分页
            CallsPager(pagerState = pagerState)
        }
    }
}

/**
 * 顶部导航栏。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar() {
    TopAppBar(
        title = {
            Text(stringResource(CallsRoute.strRes))
        },
        navigationIcon = {
            NavBeforeIconButton()
        },
    )
}

/**
 * 校区标签行。
 */
@Composable
private fun CampusTabRow(pagerState: PagerState) {
    val scope = rememberCoroutineScope()

    PrimaryTabRow(
        selectedTabIndex = pagerState.currentPage,
        divider = {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.surfaceVariant,
            )
        },
    ) {
        Campus.entries.forEachIndexed { index, campus ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                text = {
                    Text(stringResource(campus.strRes))
                },
            )
        }
    }
}

/**
 * 电话分页。
 */
@Composable
private fun CallsPager(
    pagerState: PagerState,
) {
    val clipboard = LocalClipboard.current

    val toast = koinInject<Toast>()

    val scope = rememberCoroutineScope()

    val callsList = listOf(Call.canton, Call.foshan)

    HorizontalPager(
        state = pagerState,
        beyondViewportPageCount = 1,
    ) { page ->
        // 瀑布流
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(216.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalItemSpacing = 16.dp,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(callsList[page]) {
                // 电话卡片
                CallCard(
                    onCopy = {
                        scope.launch {
                            clipboard.setClipEntry(ClipEntry(it.phoneNumber))
                            val message = getString(
                                Res.string.copied_phone_number_of,
                                getString(it.strRes),
                            )
                            toast.show(message)
                        }
                    },
                    strRes = it.strRes,
                    icon = it.icon,
                    phoneNumber = it.phoneNumber,
                    workingHours = it.workingHours,
                )
            }
        }
    }
}

/**
 * 电话卡片。
 *
 * @param strRes 名称字符串资源
 * @param icon 图标
 * @param phoneNumber 电话号码
 * @param workingHours 工作时间
 */
@Composable
private fun CallCard(
    onCopy: () -> Unit,
    strRes: StringResource,
    icon: ImageVector,
    phoneNumber: String,
    workingHours: ClosedRange<LocalTime>? = null,
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        ListItem(
            headlineContent = {
                // 名称
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        icon,
                        contentDescription = stringResource(strRes),
                        modifier = Modifier.size(16.dp),
                    )
                    Text(stringResource(strRes))
                }
            },
            supportingContent = {
                Column {
                    // 电话号码
                    Text(phoneNumber)
                    // 工作时间
                    if (workingHours != null) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.TwoTone.Work,
                                contentDescription = stringResource(Res.string.working_hours),
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                "${workingHours.start}-${workingHours.endInclusive}",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            },
            trailingContent = {
                IconButton(onClick = { onCopy() }) {
                    Icon(
                        Icons.Outlined.ContentCopy,
                        contentDescription = stringResource(Res.string.copy),
                    )
                }
            },
        )
    }
}