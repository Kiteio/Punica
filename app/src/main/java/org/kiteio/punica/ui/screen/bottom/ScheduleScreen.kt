package org.kiteio.punica.ui.screen.bottom

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material.icons.rounded.ViewWeek
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import org.kiteio.punica.R
import org.kiteio.punica.Toast
import org.kiteio.punica.candy.collectAsState
import org.kiteio.punica.candy.daysUntil
import org.kiteio.punica.candy.launchCatching
import org.kiteio.punica.datastore.Keys
import org.kiteio.punica.datastore.Preferences
import org.kiteio.punica.datastore.Timetables
import org.kiteio.punica.edu.foundation.Campus
import org.kiteio.punica.edu.foundation.Schedule
import org.kiteio.punica.edu.foundation.Semester
import org.kiteio.punica.edu.system.EduSystem
import org.kiteio.punica.edu.system.api.Timetable
import org.kiteio.punica.edu.system.api.TimetableItem
import org.kiteio.punica.edu.system.api.timetable
import org.kiteio.punica.getString
import org.kiteio.punica.getStringArray
import org.kiteio.punica.ui.LocalViewModel
import org.kiteio.punica.ui.collectAsIdentified
import org.kiteio.punica.ui.component.Dialog
import org.kiteio.punica.ui.component.DialogVisibility
import org.kiteio.punica.ui.component.DropdownMenuItem
import org.kiteio.punica.ui.component.HorizontalPager
import org.kiteio.punica.ui.component.Icon
import org.kiteio.punica.ui.component.IconText
import org.kiteio.punica.ui.component.ScaffoldColumn
import org.kiteio.punica.ui.component.SubduedText
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.component.TopAppBar
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.rememberLastUsername
import org.kiteio.punica.ui.rememberSchoolStart
import org.kiteio.punica.ui.subduedContentColor
import java.time.LocalDate

/**
 * 日程
 */
@Composable
fun ScheduleScreen() {
    val eduSystem = LocalViewModel.current.eduSystem
    var semester by remember { mutableStateOf(EduSystem.semester) }
    val timetable = Timetables.collectAsIdentified(id = rememberLastUsername(semester)) {
        eduSystem?.timetable(semester)
    }

    val preferences by Preferences.data.collectAsState()
    val week by remember {
        derivedStateOf {
            preferences?.get(Keys.schoolStart)?.let {
                LocalDate.parse(it).daysUntil(LocalDate.now()).toInt() / 7 + 1
            } ?: 0
        }
    }

    val pagerState = rememberPagerState(initialPage = 30) { 60 }
    var itemsDialogVisible by remember { mutableStateOf(false) }
    var visibleItems by remember { mutableStateOf<List<TimetableItem>?>(null) }

    ScaffoldColumn(
        topBar = {
            TopAppBar(
                pagerState = pagerState,
                week = week,
                semester = semester,
                onSemesterChange = { semester = it }
            )
        }
    ) {
        Timetable(
            pagerState = pagerState,
            week = week,
            semester = semester,
            timetable = timetable,
            onItemClick = { visibleItems = it; itemsDialogVisible = true },
            modifier = Modifier.weight(1f)
        )
        // 课表备注
        timetable?.apply {
            PlaidText(
                text = remark,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Start
            )
        }
    }

    ItemsDialog(
        visible = itemsDialogVisible,
        onDismiss = { itemsDialogVisible = false },
        items = visibleItems
    )
}


/**
 * 导航栏
 * @param pagerState
 * @param week 当前周次
 * @param semester 选中学期
 * @param onSemesterChange 学期选择事件
 */
@Composable
private fun TopAppBar(
    pagerState: PagerState,
    week: Int,
    semester: Semester,
    onSemesterChange: (Semester) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val preferences by Preferences.data.collectAsState()
    var semesterDropdownMenuExpanded by remember { mutableStateOf(false) }
    var moreDropdownMenuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(text = getString(R.string.week_count, week)) },
        shadowElevation = 0.dp,
        actions = {
            TextButton(onClick = { semesterDropdownMenuExpanded = true }) {
                Text(text = "$semester")
                preferences?.get(Keys.lastUsername)?.let { username ->
                    DropdownMenu(
                        expanded = semesterDropdownMenuExpanded,
                        onDismissRequest = { semesterDropdownMenuExpanded = false }
                    ) {
                        val firstHalf = getString(R.string.first_half)
                        val lastHalf = getString(R.string.last_half)
                        val grades = getStringArray(R.array.grades).flatMap {
                            listOf(it + firstHalf, it + lastHalf)
                        }

                        val semesters = remember(username) {
                            mutableStateListOf<Semester>().apply {
                                addAll(Semester.listFor(username))
                            }
                        }

                        semesters.forEachIndexed { index, item ->
                            DropdownMenuItem(
                                text = { Text(text = "${grades.getOrElse(index) { "" }} $item") },
                                onClick = {
                                    onSemesterChange(item)
                                    semesterDropdownMenuExpanded = false
                                },
                                selected = item == semester,
                            )
                        }
                        DropdownMenuItem(
                            text = { Text(text = getString(R.string.more)) },
                            onClick = {
                                semesters.add(semesters.last() + 1)
                            }
                        )
                    }
                }
            }

            // 更多按钮
            IconButton(onClick = { moreDropdownMenuExpanded = true }) {
                Icon(imageVector = Icons.Rounded.MoreVert)
                DropdownMenu(
                    expanded = moreDropdownMenuExpanded,
                    onDismissRequest = { moreDropdownMenuExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text(text = getString(R.string.back_to_this_week)) },
                        onClick = {
                            coroutineScope.launchCatching {
                                pagerState.scrollToPage(pagerState.pageCount / 2)
                                moreDropdownMenuExpanded = false
                            }
                        }
                    )
                }
            }
        }
    )
}


/**
 * 课表
 * @param pagerState
 * @param week 当前周次
 * @param semester
 * @param timetable
 * @param onItemClick
 * @param modifier
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
private fun Timetable(
    pagerState: PagerState,
    week: Int,
    semester: Semester,
    timetable: Timetable?,
    onItemClick: (List<TimetableItem>?) -> Unit,
    modifier: Modifier = Modifier
) {
    val height = dp4(14)
    val timeBarWidth = dp4(9)
    val daysOfWeek = getStringArray(R.array.days_of_week)
    val schoolStart = rememberSchoolStart()
    val now = LocalDate.now()

    HorizontalPager(state = pagerState, modifier = modifier) { page ->
        val offsetWeek = page - (pagerState.pageCount / 2 - week)
        val mondayDate = remember(offsetWeek, semester) {
            if (semester != EduSystem.semester) null else (schoolStart
                ?: now).plusWeeks(offsetWeek.toLong() - 1)
        }

        LazyColumn {
            // 顶部周次和星期几
            stickyHeader {
                Surface(shadowElevation = 0.8.dp) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(dp4(12))
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(timeBarWidth)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = getString(R.string.week_number, offsetWeek))
                        }
                        daysOfWeek.forEachIndexed { index, item ->
                            val date = mondayDate?.plusDays(index.toLong())

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth(1f / (7 - index))
                                    .fillMaxHeight(),
                                shadowElevation = if (date == now) 1.dp else 0.dp,
                                shape = MaterialTheme.shapes.medium,
                                color = if (date == now) MaterialTheme.colorScheme.surfaceVariant
                                else MaterialTheme.colorScheme.surface
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,

                                    ) {
                                    CompositionLocalProvider(
                                        value = LocalContentColor provides
                                                if (date == now) MaterialTheme.colorScheme.primary
                                                else LocalContentColor.current
                                    ) {
                                        Text(
                                            text = item,
                                            fontWeight = FontWeight.Black.takeIf { date == now }
                                        )
                                        AnimatedVisibility(visible = date != null) {
                                            if (date != null) PlaidText(
                                                text = "${date.month.value}-${date.dayOfMonth}",
                                                color = subduedContentColor()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item {
                Row {
                    TimeBar(width = timeBarWidth, itemHeight = height * 2)
                    FlowColumn(maxItemsInEachColumn = 6, modifier = Modifier.weight(1f)) {
                        timetable?.items?.forEach {
                            it.Plaid(
                                week = offsetWeek,
                                onClick = { onItemClick(it) },
                                height = height
                            )
                        }
                    }
                }
            }
        }
    }
}


/**
 * 左侧时间条
 * @param width
 * @param itemHeight
 */
@Composable
private fun TimeBar(width: Dp, itemHeight: Dp) {
    val coroutineScope = rememberCoroutineScope()
    val preferences by Preferences.data.collectAsState()
    val campusId by remember { derivedStateOf { preferences?.get(Keys.campusId) } }

    Column {
        remember(campusId) { Schedule.getById(campusId) }.items.forEachIndexed { index, section ->
            Surface(
                modifier = Modifier
                    .border(0.5.dp, MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        coroutineScope.launchCatching {
                            Preferences.edit {
                                it[Keys.campusId] =
                                    if (campusId == Campus.Canton.id) Campus.Foshan.id
                                    else Campus.Canton.id
                            }
                            Toast(
                                getString(
                                    R.string.schedule_change_to,
                                    getString(Campus.getById(campusId).nameResId)
                                )
                            ).show()
                        }
                    }
            ) {
                Column(
                    modifier = Modifier
                        .width(width)
                        .height(itemHeight),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    PlaidText(text = "${index * 2 + 1}", color = subduedContentColor())
                    PlaidText(text = "${section.first.first}")
                    PlaidText(text = "${section.first.second}")
                    Spacer(modifier = Modifier.height(dp4()))
                    PlaidText(text = "${index * 2 + 2}", color = subduedContentColor())
                    PlaidText(text = "${section.second.first}")
                    PlaidText(text = "${section.second.second}")
                }
            }
        }
    }
}


/**
 * 单个格子
 * @receiver [List]<[TimetableItem]>?
 * @param week 当前周次
 * @param onClick
 * @param height
 */
@Composable
private fun List<TimetableItem>?.Plaid(week: Int, onClick: () -> Unit, height: Dp) {
    var lastWeight = 0

    this?.firstOrNull { it.weeks.contains(week) }?.apply {
        ElevatedCard(
            onClick = onClick,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(1f / 7)
        ) {
            Box {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height * section.size.also { lastWeight = it })
                ) {
                    PlaidText(text = name, maxLines = 2, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(dp4(2)))
                    PlaidText(
                        text = area.replace(Regex("[(（].*?[）)]"), ""),
                        maxLines = 3,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black
                    )
                }

                if (size > 1) SubduedText(
                    text = "+${size - 1}",
                    modifier = Modifier.align(Alignment.BottomEnd),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    } ?: run {
        val weight = when (lastWeight) {
            1, 3 -> 1
            4 -> 0
            else -> 2
        }.also { lastWeight = it }

        Spacer(
            modifier = Modifier
                .fillMaxWidth(1f / 7)
                .height(height * weight)
        )
    }
}


/**
 * 格子中的文字
 * @param text
 * @param color
 * @param fontWeight
 * @param textAlign
 * @param maxLines
 */
@Composable
private fun PlaidText(
    text: String,
    color: Color = LocalContentColor.current,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign = TextAlign.Center,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        text = text,
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.labelSmall
    )
}


/**
 * 课程信息
 * @param visible
 * @param onDismiss
 * @param items
 */
@Composable
private fun ItemsDialog(visible: Boolean, onDismiss: () -> Unit, items: List<TimetableItem>?) {
    DialogVisibility(visible = visible) {
        if (items != null) {
            Dialog(
                text = {
                    items.forEach {
                        ElevatedCard(
                            modifier = Modifier.padding(dp4())
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dp4(2))
                            ) {
                                Title(text = it.name)
                                Spacer(modifier = Modifier.height(dp4(2)))
                                IconText(
                                    text = it.teacher,
                                    leadingText = getString(R.string.teacher),
                                    leadingIcon = Icons.Rounded.Person
                                )
                                Spacer(modifier = Modifier.height(dp4()))
                                IconText(
                                    text = it.area,
                                    leadingText = getString(R.string.area),
                                    leadingIcon = Icons.Rounded.LocationOn
                                )
                                Spacer(modifier = Modifier.height(dp4()))
                                IconText(
                                    text = it.weeksStr,
                                    leadingText = getString(R.string.weeks),
                                    leadingIcon = Icons.Rounded.ViewWeek
                                )
                                Spacer(modifier = Modifier.height(dp4()))
                                IconText(
                                    text = it.section.joinToString("-"),
                                    leadingText = getString(R.string.section),
                                    leadingIcon = Icons.Rounded.Timeline
                                )
                            }
                        }
                    }
                },
                onDismiss = onDismiss
            )
        }
    }
}