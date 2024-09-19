package org.kiteio.punica.ui.screen.module

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AlarmOn
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material.icons.rounded.CoPresent
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.Diversity2
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.PermIdentity
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.Timelapse
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import io.ktor.client.network.sockets.ConnectTimeoutException
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.kiteio.punica.R
import org.kiteio.punica.candy.LocalDateTime
import org.kiteio.punica.candy.format
import org.kiteio.punica.candy.route
import org.kiteio.punica.datastore.SecondClassReports
import org.kiteio.punica.edu.SecondClass
import org.kiteio.punica.edu.SecondClassActivity
import org.kiteio.punica.edu.SecondClassActivityItem
import org.kiteio.punica.edu.SecondClassLog
import org.kiteio.punica.edu.SecondClassReport
import org.kiteio.punica.edu.WebVPN
import org.kiteio.punica.getString
import org.kiteio.punica.openUri
import org.kiteio.punica.ui.collectAsIdentified
import org.kiteio.punica.ui.component.BottomSheet
import org.kiteio.punica.ui.component.Icon
import org.kiteio.punica.ui.component.IconText
import org.kiteio.punica.ui.component.Image
import org.kiteio.punica.ui.component.LinearProgressIndicator
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.component.SubduedText
import org.kiteio.punica.ui.component.TabPager
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.component.rememberTabPagerState
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.navigation.Route
import org.kiteio.punica.ui.rememberLastUser
import org.kiteio.punica.ui.rememberRemoteList

/**
 * 第二课堂
 */
@Composable
fun SecondClassScreen() {
    val lastUser = rememberLastUser()
    var secondClass by remember { mutableStateOf<SecondClass?>(null) }
    val secondClassReport = SecondClassReports.collectAsIdentified(proxiedAPIOwner = secondClass) {
        report()
    }
    val activities = rememberRemoteList(key1 = secondClass) { secondClass?.activities() }
    val logs = rememberRemoteList(key1 = secondClass) { secondClass?.log() }

    val tabPagerState = rememberTabPagerState(R.string.report, R.string.activity, R.string.log)

    LaunchedEffect(key1 = lastUser) {
        lastUser?.run {
            flow {
                emit(SecondClass.login(name, secondClassPwd, cookies, false))
            }.cancellable().catch {
                if (it is ConnectTimeoutException) {
                    WebVPN.login(name, pwd, cookies)
                    emit(SecondClass.login(name, secondClassPwd, cookies, true))
                }
            }.catch {
                secondClass = null
            }.collect {
                secondClass = it
            }
        } ?: run { secondClass = null }
    }

    ScaffoldBox(
        topBar = {
            NavBackTopAppBar(route = Route.Module.SecondClass, shadowElevation = 0.dp)
        }
    ) {
        TabPager(state = tabPagerState, tabContent = { Text(text = getString(it)) }) { page ->
            when (page) {
                0 -> Report(secondClassReport = secondClassReport)
                1 -> Activity(secondClass = secondClass, activities = activities)
                2 -> Log(logs = logs)
            }
        }
    }
}


/**
 * 成绩单
 * @param secondClassReport
 */
@Composable
private fun Report(secondClassReport: SecondClassReport?) {
    val icons = listOf(
        Icons.Rounded.Flag,
        Icons.Rounded.TipsAndUpdates,
        Icons.Rounded.VolunteerActivism,
        Icons.Rounded.Diversity2,
        Icons.Rounded.ColorLens,
        Icons.Rounded.Spa,
        Icons.Rounded.AutoGraph
    )

    LazyColumn(contentPadding = PaddingValues(dp4(2))) {
        if (secondClassReport == null) item { LinearProgressIndicator() }
        else {
            itemsIndexed(secondClassReport.items) { index, item ->
                ElevatedCard(onClick = {}, modifier = Modifier.padding(dp4(2))) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dp4(4)),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            Icon(
                                imageVector = icons[index],
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(dp4(2)))
                            Title(text = item.name)
                        }
                        Text(
                            text = "${item.score} / ${item.requiredScore}",
                            color = if (item.score / item.requiredScore >= 1) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}


/**
 * 活动
 * @param secondClass
 * @param activities
 */
@Composable
private fun Activity(secondClass: SecondClass?, activities: List<SecondClassActivityItem>) {
    var activityBottomSheetVisible by remember { mutableStateOf(false) }
    var visibleId by remember { mutableStateOf<String?>(null) }

    LazyColumn(contentPadding = PaddingValues(dp4(2))) {
        items(activities) {
            ElevatedCard(
                onClick = {
                    visibleId = it.id
                    activityBottomSheetVisible = true
                },
                modifier = Modifier.padding(dp4(2))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dp4(4))
                ) {
                    Row {
                        Image(
                            painter = rememberAsyncImagePainter(model = it.logo),
                            modifier = Modifier
                                .size(dp4(20))
                                .clip(MaterialTheme.shapes.medium)
                        )
                        Spacer(modifier = Modifier.width(dp4(4)))
                        Column(modifier = Modifier.weight(1f)) {
                            CompositionLocalProvider(
                                value = LocalTextStyle provides MaterialTheme.typography.bodyMedium
                            ) {
                                Text(text = it.name)
                                Spacer(modifier = Modifier.height(dp4()))

                                Title(
                                    text = "${it.sort}  ${it.score}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(dp4(4)))

                    SubduedText(
                        text = LocalDateTime(it.start).format() +
                                " - " + LocalDateTime(it.end).format()
                    )
                    SubduedText(
                        text = getString(
                            if (it.isOnline) R.string.online else R.string.offline
                        ) + "  " + it.type + "  " + it.organization
                    )
                }
            }
        }
    }

    ActivityBottomSheet(
        visible = activityBottomSheetVisible,
        onDismiss = { activityBottomSheetVisible = false },
        secondClass = secondClass,
        id = visibleId
    )
}


@Composable
private fun ActivityBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    secondClass: SecondClass?,
    id: String?
) {
    BottomSheet(visible = visible, onDismiss = onDismiss, skipPartiallyExpanded = true) {
        var activity by remember { mutableStateOf<SecondClassActivity?>(null) }

        LaunchedEffect(key1 = secondClass, key2 = id) {
            id?.let { activity = secondClass?.activity(it) }
        }

        LazyColumn(modifier = Modifier.padding(dp4(4))) {
            activity?.run {
                item {
                    Title(text = name)
                    Spacer(modifier = Modifier.height(dp4(4)))
                }

                item {
                    Text(text = "$start - $end")
                    Title(text = "$sort  $score", style = MaterialTheme.typography.bodyMedium)
                    SubduedText(text = type)
                    if (submit) SubduedText(
                        text = getString(R.string.assignments_must_be_submitted)
                    )
                    Spacer(modifier = Modifier.height(dp4(4)))
                }

                item {
                    IconText(
                        text = deadline,
                        leadingIcon = Icons.Rounded.AlarmOn,
                        leadingText = getString(R.string.deadline)
                    )
                    IconText(
                        text = "$num / $maxNum",
                        leadingIcon = Icons.Rounded.Person,
                        leadingText = getString(R.string.limit)
                    )
                    IconText(
                        text = area,
                        leadingIcon = Icons.Rounded.LocationOn,
                        leadingText = getString(R.string.area)
                    )
                    Spacer(modifier = Modifier.height(dp4(4)))
                }

                item {
                    IconText(
                        text = owner,
                        leadingIcon = Icons.Rounded.PermIdentity,
                        leadingText = getString(R.string.admin)
                    )
                    IconText(
                        text = phoneNumber,
                        leadingIcon = Icons.Rounded.Phone,
                        leadingText = getString(R.string.phone_number)
                    )
                    IconText(
                        text = teacher,
                        leadingIcon = Icons.Rounded.CoPresent,
                        leadingText = getString(R.string.teacher)
                    )
                    IconText(
                        text = trainingHours.toString(),
                        leadingIcon = Icons.Rounded.Timelapse,
                        leadingText = getString(R.string.training_hours)
                    )
                    IconText(
                        text = organization,
                        leadingIcon = Icons.Rounded.Groups,
                        leadingText = getString(R.string.organizers)
                    )
                    Spacer(modifier = Modifier.height(dp4(4)))
                }

                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        ElevatedButton(
                            onClick = { openUri(SecondClass.route { "#/pages/acti/info?key=$id" }) }
                        ) { Text(text = getString(R.string.open_in_browser)) }
                    }
                    Spacer(modifier = Modifier.height(dp4(4)))
                }

                item {
                    SubduedText(text = desc)
                }
            }
        }
    }
}


/**
 * 日志
 * @param logs
 */
@Composable
private fun Log(logs: List<SecondClassLog>) {
    LazyColumn(contentPadding = PaddingValues(dp4(2))) {
        items(logs) {
            ElevatedCard(onClick = {}, modifier = Modifier.padding(dp4(2))) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dp4(4)),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Title(text = it.sort)
                        Spacer(modifier = Modifier.height(dp4()))
                        SubduedText(text = it.name)
                        Spacer(modifier = Modifier.height(dp4()))
                        SubduedText(text = LocalDateTime(it.time).format())
                        SubduedText(text = it.term)
                    }
                    Spacer(modifier = Modifier.width(dp4(4)))
                    Title(text = "${it.score}")
                }
            }
        }
    }
}