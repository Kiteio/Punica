package org.kiteio.punica.mirror.ui.screen.modules.cet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.twotone.Looks4
import androidx.compose.material.icons.twotone.Looks6
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.datetime.format.AmPmMarker
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.mirror.modal.cet.CetExam
import org.kiteio.punica.mirror.modal.cet.CetExamTime
import org.kiteio.punica.mirror.ui.component.ErrorContent
import org.kiteio.punica.mirror.ui.component.LoadingContent
import org.kiteio.punica.mirror.ui.component.NavBeforeIconButton
import org.kiteio.punica.mirror.ui.navigation.ModuleNavKey
import org.koin.compose.viewmodel.koinViewModel
import punica.composeapp.generated.resources.*

/**
 * 四六级页入口。
 */
fun EntryProviderScope<NavKey>.cetEntry() {
    entry<CetRoute> { CetScreen() }
}

/**
 * 四六级页路由。
 */
@Serializable
data object CetRoute : ModuleNavKey {
    override val strRes = Res.string.cet
    override val icon = Icons.Outlined.Verified
}

@Composable
private fun CetScreen() {
    val cetViewModel = koinViewModel<CetViewModel>()

    val uiState by cetViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        // 加载四六级考试
        cetViewModel.dispatch(CetIntent.Load)
    }

    Scaffold(
        topBar = { TopAppBar() },
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        when (uiState) {
            CetUiState.Loading -> {
                // 加载中
                LoadingContent(modifier = modifier)
            }

            is CetUiState.Success -> {
                // 加载成功
                CetExamContent(
                    cetExam = (uiState as CetUiState.Success).cetExam,
                    modifier = modifier,
                )
            }

            is CetUiState.Error -> {
                // 加载失败
                ErrorContent(
                    throwable = (uiState as CetUiState.Error).e,
                    modifier = modifier,
                )
            }
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
            Text(stringResource(CetRoute.strRes))
        },
        navigationIcon = {
            NavBeforeIconButton()
        },
    )
}

/**
 * Cet 考试内容。
 *
 * @param cetExam Cet 考试
 */
@Composable
private fun CetExamContent(
    cetExam: CetExam,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(184.dp),
            contentPadding = PaddingValues(16.dp),
            verticalItemSpacing = 16.dp,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(span = StaggeredGridItemSpan.FullLine) {
                // 考试名称
                Text(
                    cetExam.name,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            item {
                // 四级笔试
                CetExamCard(
                    name = stringResource(Res.string.cet4_written),
                    icon = Icons.TwoTone.Looks4,
                    cetExamTime = cetExam.cet4Written,
                )
            }
            item {
                // 六级笔试
                CetExamCard(
                    name = stringResource(Res.string.cet6_written),
                    icon = Icons.TwoTone.Looks6,
                    cetExamTime = cetExam.cet6Written,
                )
            }
            item {
                // 四级口语
                CetExamCard(
                    name = stringResource(Res.string.cet4_speaking),
                    icon = Icons.TwoTone.Looks4,
                    cetExamTime = cetExam.cet4Speaking,
                )
            }
            item {
                // 六级口语
                CetExamCard(
                    name = stringResource(Res.string.cet6_speaking),
                    icon = Icons.TwoTone.Looks6,
                    cetExamTime = cetExam.cet6Speaking,
                )
            }
            item(span = StaggeredGridItemSpan.FullLine) {
                // 备注
                Text(
                    cetExam.note,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

/**
 * Cet 考试卡片。
 */
@Composable
private fun CetExamCard(
    name: String,
    icon: ImageVector,
    cetExamTime: CetExamTime,
) {
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        border = BorderStroke(
            0.5.dp,
            MaterialTheme.colorScheme.surfaceVariant,
        ),
        shadowElevation = 1.dp,
    ) {
        ListItem(
            headlineContent = {
                // 考试名称
                Text(name)
            },
            supportingContent = {
                // 考试时间
                Text(
                    buildAnnotatedString {
                        // 日期
                        append("${cetExamTime.date}")
                        // 早上或下午
                        when (cetExamTime.amPmMarker) {
                            AmPmMarker.AM -> append(" ${stringResource(Res.string.am)}")
                            AmPmMarker.PM -> append(" ${stringResource(Res.string.pm)}")
                            null -> {}
                        }
                    }
                )
            },
            leadingContent = {
                // 考试图标
                Icon(icon, contentDescription = name)
            },
        )
    }
}