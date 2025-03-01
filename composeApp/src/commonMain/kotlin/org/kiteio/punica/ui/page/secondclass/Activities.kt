package org.kiteio.punica.ui.page.secondclass

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.client.secondclass.api.Activity
import org.kiteio.punica.client.secondclass.api.ActivityProfile
import org.kiteio.punica.client.secondclass.api.getActivities
import org.kiteio.punica.client.secondclass.api.getActivityProfile
import org.kiteio.punica.ui.component.LoadingNotNullOrEmpty
import org.kiteio.punica.ui.component.ModalBottomSheet
import org.kiteio.punica.ui.component.ProvideBodyMedium
import org.kiteio.punica.ui.component.SpaceBetween
import org.kiteio.punica.wrapper.launchCatching
import punica.composeapp.generated.resources.*

/**
 * 活动。
 */
@Composable
fun SecondClassVM.Activities() {
    var isLoading by remember { mutableStateOf(true) }
    val activitiesOrNull by produceState<List<Activity>?>(null, secondClass) {
        launchCatching {
            try {
                value = secondClass?.getActivities()
            } finally {
                isLoading = false
            }
        }
    }

    var bottomSheetVisible by remember { mutableStateOf(false) }
    var activity by remember { mutableStateOf<Activity?>(null) }

    LoadingNotNullOrEmpty(
        activitiesOrNull,
        isLoading = isLoading,
        modifier = Modifier.fillMaxSize(),
    ) { activities ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(200.dp),
            contentPadding = PaddingValues(4.dp),
        ) {
            items(activities) {
                Activity(
                    it,
                    onClick = {
                        activity = it
                        bottomSheetVisible = true
                    },
                    modifier = Modifier.padding(4.dp),
                )
            }
        }
    }

    ActivityBottomSheet(
        bottomSheetVisible,
        onDismissRequest = {
            bottomSheetVisible = false
            activity = null
        },
        activity = activity,
    )
}


/**
 * 活动。
 */
@Composable
private fun Activity(activity: Activity, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(onClick = onClick, modifier = modifier) {
        ListItem(
            headlineContent = { Text(activity.name) },
            supportingContent = {
                Column {
                    // 时间
                    Text("${activity.duration.start} - ${activity.duration.endInclusive}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        buildAnnotatedString {
                            // 分类和分数
                            withStyle(
                                LocalTextStyle.current.copy(
                                    fontWeight = FontWeight.Bold
                                ).toSpanStyle(),
                            ) {
                                append(activity.category)
                                append("  ")
                                append("${activity.score}")
                                append("  ")
                            }
                            // 线上线下
                            append(stringResource(if (activity.isOnline) Res.string.online else Res.string.offline))
                            append("  ")
                            // 活动类型
                            append(activity.type)
                        },
                    )
                }
            },
        )
    }
}


/**
 * 活动详情模态对话框。
 */
@Composable
private fun SecondClassVM.ActivityBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    activity: Activity?,
) {
    if (visible) {
        val activityProfile by produceState<ActivityProfile?>(null, secondClass) {
            launchCatching {
                activity?.run {
                    value = secondClass?.getActivityProfile(id)
                }
            }
        }

        ModalBottomSheet(
            visible = activityProfile != null,
            onDismissRequest = onDismissRequest,
        ) {
            ActivityProfile(activityProfile!!)
        }
    }
}


/**
 * 活动详情。
 */
@Composable
private fun ActivityProfile(activityProfile: ActivityProfile) = with(activityProfile) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        item {
            // 名称
            Text(name)
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            ProvideBodyMedium {
                // 时间
                Text("${duration.start} - ${duration.endInclusive}")
                // 地点
                Text(area)
            }
        }
        item {
            ProvideBodyMedium {
                // 分类、分数
                Text(
                    buildAnnotatedString {
                        // 分类和分数
                        withStyle(
                            LocalTextStyle.current.copy(
                                fontWeight = FontWeight.Bold
                            ).toSpanStyle(),
                        ) {
                            append(category)
                            append("  ")
                            append("$score")
                            append("  ")
                        }
                        // 活动类型
                        append(type)
                    },
                )
                // 是否必须提交作业
                if (needSubmit) {
                    Text(stringResource(Res.string.need_submit))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            ProvideBodyMedium {
                // 报名截止时间
                SpaceBetween {
                    Text(stringResource(Res.string.application_deadline))
                    Text(deadline)
                }
                // 名额
                SpaceBetween {
                    Text(stringResource(Res.string.place))
                    Text("$leftover / $total")
                }
            }
        }
        item {
            ProvideBodyMedium {
                // 管理员
                SpaceBetween {
                    Text(stringResource(Res.string.admin))
                    Text(admin)
                }
                // 手机号
                SpaceBetween {
                    Text(stringResource(Res.string.phone_number))
                    Text(phoneNumber)
                }
            }
        }
        item {
            ProvideBodyMedium {
                // 指导老师
                teacher?.let {
                    SpaceBetween {
                        Text(stringResource(Res.string.coach))
                        Text(it)
                    }
                }
                // 培训时间
                if (trainingHours > 0) {
                    SpaceBetween {
                        Text(stringResource(Res.string.training_hours))
                        Text("$trainingHours h")
                    }
                }
            }
        }
        item {
            ProvideBodyMedium {
                // 主办方
                SpaceBetween {
                    Text(stringResource(Res.string.host))
                    Text(host)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            // 描述
            Text(description, style = MaterialTheme.typography.bodySmall)
        }
    }
}





