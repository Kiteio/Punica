package org.kiteio.punica.ui.screen.module

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.Timelapse
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.paging.Pager
import androidx.paging.compose.collectAsLazyPagingItems
import org.kiteio.punica.R
import org.kiteio.punica.Toast
import org.kiteio.punica.candy.catch
import org.kiteio.punica.candy.launchCatch
import org.kiteio.punica.datastore.Courses
import org.kiteio.punica.datastore.Tokens
import org.kiteio.punica.datastore.get
import org.kiteio.punica.datastore.remove
import org.kiteio.punica.datastore.set
import org.kiteio.punica.edu.foundation.Campus
import org.kiteio.punica.edu.system.CourseSystem
import org.kiteio.punica.edu.system.api.Token
import org.kiteio.punica.edu.system.api.course.Course
import org.kiteio.punica.edu.system.api.course.MyCourse
import org.kiteio.punica.edu.system.api.course.Priority
import org.kiteio.punica.edu.system.api.course.SearchParams
import org.kiteio.punica.edu.system.api.course.Section
import org.kiteio.punica.edu.system.api.course.Sort
import org.kiteio.punica.edu.system.api.course.delete
import org.kiteio.punica.edu.system.api.course.exit
import org.kiteio.punica.edu.system.api.course.list
import org.kiteio.punica.edu.system.api.course.log
import org.kiteio.punica.edu.system.api.course.myCourses
import org.kiteio.punica.edu.system.api.course.overview
import org.kiteio.punica.edu.system.api.course.search
import org.kiteio.punica.edu.system.api.course.select
import org.kiteio.punica.edu.system.api.course.table
import org.kiteio.punica.getString
import org.kiteio.punica.getStringArray
import org.kiteio.punica.ui.LocalNavController
import org.kiteio.punica.ui.LocalViewModel
import org.kiteio.punica.ui.collectAsIdentifiedList
import org.kiteio.punica.ui.component.BottomSheet
import org.kiteio.punica.ui.component.CheckBox
import org.kiteio.punica.ui.component.Dialog
import org.kiteio.punica.ui.component.DialogVisibility
import org.kiteio.punica.ui.component.DropdownMenuItem
import org.kiteio.punica.ui.component.Icon
import org.kiteio.punica.ui.component.IconText
import org.kiteio.punica.ui.component.KVText
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.Pager
import org.kiteio.punica.ui.component.PagingSource
import org.kiteio.punica.ui.component.RadioButton
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.component.SubduedText
import org.kiteio.punica.ui.component.TabPager
import org.kiteio.punica.ui.component.TextField
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.component.items
import org.kiteio.punica.ui.component.rememberTabPagerState
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.navigation.Route
import org.kiteio.punica.ui.rememberRemote
import org.kiteio.punica.ui.rememberRemoteList
import org.kiteio.punica.ui.runWithReLogin
import java.time.DayOfWeek

/**
 * 选课系统
 */
@Composable
fun CourseSystemScreen() {
    val coroutineScope = rememberCoroutineScope()
    val navController = LocalNavController.current
    val eduSystem = LocalViewModel.current.eduSystem
    var courseSystem by remember { mutableStateOf<CourseSystem?>(null) }

    LaunchedEffect(key1 = eduSystem) {
        eduSystem?.runWithReLogin {
            // 进入选课系统并保存 Token
            courseSystem = catch({ null }) {
                CourseSystem.from(eduSystem)
                    .also { courseSystem -> Tokens.edit { it.set(courseSystem.token) } }
            }

            // 通过 Token 进入选课系统
            if (courseSystem == null) {
                Tokens.data.collect {
                    it.get<Token>(eduSystem.name)?.let { token ->
                        courseSystem = catch { CourseSystem.from(eduSystem, token) }
                    }
                }
            }
        }
    }

    BackHandler(enabled = courseSystem != null) {
        // 退出选课系统
        coroutineScope.launchCatch { courseSystem?.exit() }
        navController.popBackStack()
    }

    val tabPagerState = rememberTabPagerState(
        R.string.favorites,
        R.string.course_basic,
        R.string.course_optional,
        R.string.course_general,
        R.string.course_cross_major,
        R.string.course_cross_year,
        R.string.course_major
    )

    var myCoursesBottomSheetVisible by remember { mutableStateOf(false) }
    var logsBottomSheetVisible by remember { mutableStateOf(false) }
    var infoDialogVisible by remember { mutableStateOf(false) }

    ScaffoldBox(
        topBar = {
            NavBackTopAppBar(
                route = Route.Module.CourseSystem,
                actions = {
                    courseSystem?.run {
                        IconButton(onClick = { myCoursesBottomSheetVisible = true }) {
                            Icon(imageVector = Icons.Rounded.BookmarkBorder)
                        }
                        IconButton(onClick = { logsBottomSheetVisible = true }) {
                            Icon(imageVector = Icons.Rounded.History)
                        }
                        IconButton(onClick = { infoDialogVisible = true }) {
                            Icon(imageVector = Icons.Outlined.Info)
                        }
                    }
                },
                shadowElevation = 0.dp
            )
        }
    ) {
        courseSystem?.run {
            val allCourses = Courses.collectAsIdentifiedList<Course>()
            val courses by remember { derivedStateOf { allCourses.filter { it.username == token.username } } }

            TabPager(state = tabPagerState, tabContent = { Text(text = getString(it)) }) { page ->
                when (page) {
                    0 -> CourseList(courses = courses)
                    1 -> CourseList(sort = Sort.Basic, courses = courses)
                    2 -> CourseList(sort = Sort.Optional, courses = courses)
                    3 -> CourseList(sort = Sort.General, courses = courses)
                    4 -> CourseList(sort = Sort.CrossMajor, courses = courses)
                    5 -> CourseList(sort = Sort.CrossYear, courses = courses)
                    6 -> CourseList(sort = Sort.Major, courses = courses)
                }
            }
        }
    }

    courseSystem?.run {
        MyCoursesBottomSheet(
            visible = myCoursesBottomSheetVisible,
            onDismiss = { myCoursesBottomSheetVisible = false })
        LogsBottomSheet(
            visible = logsBottomSheetVisible,
            onDismiss = { logsBottomSheetVisible = false })
        InfoDialog(
            visible = infoDialogVisible,
            onDismiss = { infoDialogVisible = false },
            text = "$name\n$start - $end"
        )
    }
}


/**
 * 已选课程
 * @receiver [CourseSystem]
 * @param visible
 * @param onDismiss
 */
@Composable
private fun CourseSystem.MyCoursesBottomSheet(visible: Boolean, onDismiss: () -> Unit) {
    BottomSheet(visible = visible, onDismiss = onDismiss, skipPartiallyExpanded = true) {
        val tabPagerState = rememberTabPagerState(R.string.list, R.string.timetable)
        val overview = rememberRemote(key = this@MyCoursesBottomSheet) { overview() }
        var listChangeKey by remember { mutableStateOf(false) }

        Column {
            overview?.run {
                OutlinedCard(modifier = Modifier.padding(dp4(4))) {
                    Column(modifier = Modifier.padding(dp4(4))) {
                        pointInfos.forEach {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Title(text = it.name)
                                Text(text = "${it.have} / ${it.limit}")
                            }
                        }

                        Spacer(modifier = Modifier.height(dp4(2)))
                        SubduedText(text = info)
                    }
                }
            }

            TabPager(
                state = tabPagerState,
                tabContent = { Text(text = getString(it)) }
            ) { page ->
                when (page) {
                    0 -> {
                        val coroutineScope = rememberCoroutineScope()
                        val myCourses =
                            rememberRemoteList(
                                key1 = this@MyCoursesBottomSheet,
                                key2 = listChangeKey
                            ) { myCourses() }

                        var courseDeleteDialogVisible by remember { mutableStateOf(false) }
                        var visibleMyCourse by remember { mutableStateOf<MyCourse?>(null) }

                        LazyColumn(contentPadding = PaddingValues(dp4(2))) {
                            items(myCourses) {
                                OutlinedCard(modifier = Modifier.padding(dp4(2))) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(dp4(4))
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Title(text = it.name)
                                            SubduedText(text = it.teacher)
                                            KVText(key = getString(R.string.id), value = it.id)
                                            KVText(
                                                key = getString(R.string.point),
                                                value = it.point
                                            )
                                            KVText(key = getString(R.string.type), value = it.type)

                                            if (it.time.isNotBlank()) KVText(
                                                key = getString(R.string.time),
                                                it.time
                                            )
                                            if (it.area.isNotBlank()) KVText(
                                                key = getString(R.string.area),
                                                it.area
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(dp4()))
                                        TextButton(
                                            onClick = {
                                                visibleMyCourse = it
                                                courseDeleteDialogVisible = true
                                            }
                                        ) {
                                            Text(
                                                text = getString(R.string.course_delete),
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        CourseDeleteDialog(
                            visible = courseDeleteDialogVisible,
                            onDismiss = { courseDeleteDialogVisible = false },
                            onConfirm = {
                                coroutineScope.launchCatch {
                                    visibleMyCourse?.let { myCourse ->
                                        delete(myCourse.operateId)
                                        listChangeKey = !listChangeKey
                                        Toast(R.string.unselected)
                                    }
                                    courseDeleteDialogVisible = false
                                }
                            },
                            courseName = visibleMyCourse?.name
                        )
                    }

                    1 -> {
                        val table =
                            rememberRemoteList(
                                key1 = this@MyCoursesBottomSheet,
                                key2 = listChangeKey
                            ) { table() }
                        val daysOfWeek = getStringArray(R.array.days_of_week)

                        Column(modifier = Modifier.padding(dp4(2))) {
                            Row {
                                (-1..6).forEach {
                                    OutlinedCard(
                                        shape = RectangleShape,
                                        border = BorderStroke(
                                            0.1.dp,
                                            MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(dp4(6))
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) { Text(text = daysOfWeek.getOrNull(it) ?: "") }
                                    }
                                }
                            }

                            LazyVerticalGrid(columns = GridCells.Fixed(8)) {
                                items(table) {
                                    OutlinedCard(
                                        shape = RectangleShape,
                                        border = BorderStroke(
                                            0.1.dp,
                                            MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(dp4(18)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            it?.let { text ->
                                                Text(
                                                    text = text,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontSize = 10.sp
                                                    ),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


/**
 * 退课日志
 * @param visible
 * @param onDismiss
 */
@Composable
private fun CourseSystem.LogsBottomSheet(visible: Boolean, onDismiss: () -> Unit) {
    BottomSheet(visible = visible, onDismiss = onDismiss, skipPartiallyExpanded = true) {
        val logs = rememberRemoteList(key1 = this) { log() }

        LazyColumn(contentPadding = PaddingValues(dp4(2))) {
            item {
                Title(text = getString(R.string.log), modifier = Modifier.padding(dp4(2)))
            }
            items(logs) {
                var expanded by remember { mutableStateOf(false) }

                OutlinedCard(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.padding(dp4(2))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dp4(4))
                    ) {
                        Title(text = it.time, style = MaterialTheme.typography.titleSmall)
                        SubduedText(text = "${it.operator} ${it.operation}")
                        Spacer(modifier = Modifier.height(dp4()))
                        KVText(key = getString(R.string.name), value = it.courseName)
                        KVText(key = getString(R.string.point), value = it.point)
                        KVText(key = getString(R.string.teacher), value = it.teacher)
                        if (it.time.isNotEmpty()) KVText(
                            key = getString(R.string.time),
                            value = it.classTime.joinToString("\n")
                        )

                        AnimatedVisibility(visible = expanded) {
                            Column {
                                KVText(key = getString(R.string.id), value = it.courseId)
                                KVText(key = getString(R.string.type), value = it.courseType)

                                if (it.courseSort.isNotBlank()) SubduedText(text = it.courseSort)
                                if (it.desc.isNotBlank()) SubduedText(text = it.desc)
                            }
                        }
                    }
                }
            }
        }
    }
}


/**
 * 选课信息
 * @param visible
 * @param onDismiss
 * @param text
 */
@Composable
private fun InfoDialog(visible: Boolean, onDismiss: () -> Unit, text: String) {
    DialogVisibility(visible = visible) {
        Dialog(
            text = { Text(text = text) },
            onDismiss = onDismiss,
            contentHorizontalAlignment = Alignment.Start
        )
    }
}


/**
 * 不可搜索课程列表
 * @receiver [CourseSystem]
 * @param sort
 * @param courses 收藏课程
 */
@Composable
private fun CourseSystem.CourseList(sort: Sort.Unsearchable, courses: List<Course>) {
    val pager = remember(key1 = this) {
        Pager(15) {
            UnsearchableCoursesPagingSource(this, sort)
        }
    }

    CourseList(pager = pager, courses = courses)
}


/**
 * 可搜索课程列表
 * @receiver [CourseSystem]
 * @param sort
 * @param courses
 */
@Composable
private fun CourseSystem.CourseList(sort: Sort.Searchable, courses: List<Course>) {
    var searchParams by remember { mutableStateOf(SearchParams()) }
    val pager = remember(key1 = this, key2 = searchParams) {
        Pager(15) { SearchableCoursesPagingSource(this, sort, searchParams) }
    }

    var searchParamsBottomSheetVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        CourseList(pager = pager, courses = courses)
        FloatingActionButton(
            onClick = { searchParamsBottomSheetVisible = true },
            modifier = Modifier
                .padding(dp4(4))
                .align(Alignment.BottomEnd)
        ) { Icon(Icons.Rounded.Search) }

        SearchParamsBottomSheet(
            visible = searchParamsBottomSheetVisible,
            onDismiss = { searchParamsBottomSheetVisible = false },
            searchParams = searchParams,
            onConfirm = { searchParams = it; searchParamsBottomSheetVisible = false }
        )
    }
}


@Composable
private fun SearchParamsBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    searchParams: SearchParams?,
    onConfirm: (SearchParams) -> Unit,
) {
    BottomSheet(visible = visible, onDismiss = onDismiss, skipPartiallyExpanded = true) {
        searchParams?.run {
            val daysOfWeek = getStringArray(R.array.days_of_week)

            var name by remember { mutableStateOf(name) }
            var teacher by remember { mutableStateOf(teacher) }
            var dayOfWeek by remember { mutableStateOf(dayOfWeek) }
            var section by remember { mutableStateOf(section) }
            var emptyOnly by remember { mutableStateOf(emptyOnly) }
            var filterConflicting by remember { mutableStateOf(filterConflicting) }
            var campus by remember { mutableStateOf(campus) }

            Column(modifier = Modifier.padding(dp4(4))) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = getString(R.string.course)) }
                )
                TextField(
                    value = teacher,
                    onValueChange = { teacher = it },
                    label = { Text(text = getString(R.string.teacher)) }
                )
                Spacer(modifier = Modifier.height(dp4(2)))

                Column(modifier = Modifier.padding(top = dp4(4), start = dp4(4), end = dp4(4))) {
                    Options(
                        name = getString(R.string.day_of_week, ""),
                        onSelect = { dayOfWeek = if (it == 0) null else DayOfWeek.of(it) },
                        options = listOf(
                            getString(R.string.all),
                            *DayOfWeek.entries.map { daysOfWeek[it.ordinal] }.toTypedArray()
                        ),
                        toString = { it },
                        selectedIndex = dayOfWeek?.ordinal?.plus(1) ?: 0
                    )
                    Options(
                        name = getString(R.string.section),
                        onSelect = { section = Section.entries[it] },
                        options = Section.entries,
                        toString = { if (it == Section.Unspecified) getString(R.string.all) else it.value },
                        selectedIndex = section.ordinal
                    )
                    Options(
                        name = getString(R.string.campus),
                        onSelect = { campus = if (it == 0) null else Campus.getById(it) },
                        options = listOf(
                            R.string.all,
                            *Campus.values.map { it.nameResId }.toTypedArray()
                        ),
                        toString = { getString(it) },
                        selectedIndex = campus?.id ?: 0
                    )

                    CheckBox(
                        checked = emptyOnly,
                        onCheckedChange = { emptyOnly = it },
                        label = getString(R.string.filtering_no_seats),
                        reverse = true
                    )
                    CheckBox(
                        checked = filterConflicting,
                        onCheckedChange = { filterConflicting = it },
                        label = getString(R.string.filter_conflicting),
                        reverse = true
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dp4(10))
                ) {
                    ElevatedButton(
                        onClick = {
                            onConfirm(SearchParams())
                            Toast(R.string.reset_complete).show()
                        }
                    ) { Text(text = getString(R.string.reset)) }

                    Spacer(Modifier.width(dp4(8)))

                    ElevatedButton(
                        onClick = {
                            onConfirm(
                                SearchParams(
                                    name, teacher, dayOfWeek, section, emptyOnly,
                                    filterConflicting, campus
                                )
                            )
                            Toast(R.string.saved).show()
                        }
                    ) { Text(text = getString(R.string.save)) }
                }
            }
        }
    }
}


/**
 * 选项
 * @param name 名称
 * @param onSelect
 * @param options 可选项
 * @param toString [T] toString
 * @param selectedIndex
 */
@Composable
private fun <T> Options(
    name: String,
    onSelect: (Int) -> Unit,
    options: List<T>,
    toString: (T) -> String,
    selectedIndex: Int
) {
    var expanded by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = name)
        Spacer(modifier = Modifier.width(dp4(2)))
        TextButton(onClick = { expanded = true }) {
            Text(text = toString(options[selectedIndex]))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = { Text(text = toString(item)) },
                    onClick = { onSelect(index); expanded = false },
                    selected = selectedIndex == index
                )
            }
        }
    }
}


/**
 * 课程列表
 * @receiver [CourseSystem]
 * @param pager
 * @param courses 收藏课程
 */
@Composable
private fun CourseSystem.CourseList(pager: Pager<Int, Course>, courses: List<Course>) {
    val items = pager.flow.collectAsLazyPagingItems()

    CourseListOuter(getStared = { operateId -> courses.any { it.operateId == operateId } }) { itemContent ->
        items(items) { itemContent(it) }
    }
}


/**
 * 收藏课程列表
 * @receiver [CourseSystem]
 * @param courses 收藏课程
 */
@Composable
private fun CourseSystem.CourseList(courses: List<Course>) {
    CourseListOuter(getStared = { true }) { itemContent ->
        items(courses) { itemContent(it) }
    }
}


/**
 * 课程列表外壳
 * @receiver [CourseSystem]
 * @param getStared
 * @param items
 */
@Composable
private fun CourseSystem.CourseListOuter(
    getStared: (operateId: String) -> Boolean,
    items: LazyListScope.(itemContent: @Composable LazyItemScope.(Course) -> Unit) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var visibleCourse by remember { mutableStateOf<Course?>(null) }
    var courseBottomSheetVisible by remember { mutableStateOf(false) }
    var priorityDialogVisible by remember { mutableStateOf(false) }

    LazyColumn(contentPadding = PaddingValues(dp4(2))) {
        items { course ->
            Course(
                course = course,
                onClick = { visibleCourse = course; courseBottomSheetVisible = true },
                onSelect = {
                    if (course.selectable) {
                        coroutineScope.launchCatch {
                            select(course.operateId, course.sort, null)
                            course.selected.value = true
                            Toast(R.string.selected_).show()
                        }
                    } else {
                        visibleCourse = course
                        priorityDialogVisible = true
                    }
                },
                stared = getStared(course.operateId),
                onStaredChange = { stared ->
                    coroutineScope.launchCatch {
                        Courses.edit {
                            if (stared) it.set(course)
                            else it.remove(course)
                        }
                    }
                },
                modifier = Modifier.animateItem()
            )
        }
    }

    CourseBottomSheet(
        visible = courseBottomSheetVisible,
        onDismiss = { courseBottomSheetVisible = false; visibleCourse = null },
        course = visibleCourse
    )

    PriorityDialog(
        visible = priorityDialogVisible,
        onDismiss = { priorityDialogVisible = false },
        onConfirm = {
            coroutineScope.launchCatch {
                visibleCourse?.run {
                    select(operateId, sort, it)
                    selected.value = true
                    Toast(R.string.selected_).show()
                }
                priorityDialogVisible = false
            }
        }
    )
}


/**
 * 课程项
 * @param course
 * @param onClick
 * @param onSelect
 * @param stared 是否已收藏
 * @param onStaredChange
 * @param modifier
 */
@Composable
private fun Course(
    course: Course,
    onClick: () -> Unit,
    onSelect: () -> Unit,
    stared: Boolean,
    onStaredChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.padding(dp4(2))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dp4(4)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Title(text = course.name, maxLines = 1)
                Spacer(modifier = Modifier.height(dp4()))

                Row {
                    KVText(
                        key = getString(R.string.id),
                        value = course.courseId,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(dp4(2)))
                    KVText(
                        key = getString(R.string.point),
                        value = course.point,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row {
                    KVText(
                        key = getString(R.string.teacher),
                        value = course.teacher,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(dp4(2)))
                    KVText(
                        key = getString(R.string.limit),
                        value = "${course.remaining} / ${course.total}",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(dp4()))

                if (course.time.isNotBlank()) KVText(
                    key = getString(R.string.time),
                    value = course.time
                )
                if (course.area.isNotBlank()) KVText(
                    key = getString(R.string.area),
                    value = course.area
                )

                if (course.status.isNotBlank() && !course.selected.value) {
                    Spacer(modifier = Modifier.height(dp4()))
                    SubduedText(text = course.status)
                }
            }
            Spacer(modifier = Modifier.width(dp4()))

            IconToggleButton(checked = stared, onCheckedChange = onStaredChange) {
                Icon(imageVector = if (stared) Icons.Rounded.Star else Icons.Rounded.StarBorder)
            }

            TextButton(onClick = onSelect, enabled = !course.selected.value) {
                Text(
                    text = getString(
                        if (course.selected.value) R.string.selected_ else R.string.course_select
                    )
                )
            }
        }
    }
}


/**
 * 课程详情
 * @receiver [CourseSystem]
 * @param visible
 * @param onDismiss
 * @param course
 */
@Composable
private fun CourseSystem.CourseBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    course: Course?
) {
    BottomSheet(visible = visible, onDismiss = onDismiss, skipPartiallyExpanded = true) {
        course?.run {
            val coroutineScope = rememberCoroutineScope()
            var courseDeleteDialogVisible by remember { mutableStateOf(false) }

            Column(modifier = Modifier.padding(dp4(4))) {
                Column {
                    Title(text = name)
                    Spacer(modifier = Modifier.height(dp4(2)))

                    SubduedText(text = "${getString(R.string.id)} $courseId")
                    SubduedText(text = "${getString(R.string.limit)} $remaining / $total")
                    SubduedText(text = "${getString(R.string.department)} $department")
                    SubduedText(text = "${getString(R.string.class_hours)} $classHours")
                    SubduedText(text = "${getString(R.string.exam_mode)} $examMode")

                    if (status.isNotBlank() && !course.selected.value) {
                        SubduedText(text = status)
                    }
                    Spacer(modifier = Modifier.height(dp4(2)))

                    CompositionLocalProvider(
                        value = LocalTextStyle provides MaterialTheme.typography.bodyMedium
                    ) {
                        IconText(
                            text = teacher,
                            leadingIcon = Icons.Rounded.AccountBox,
                            leadingText = getString(R.string.teacher)
                        )
                        if (time.isNotBlank()) IconText(
                            text = time,
                            leadingIcon = Icons.Rounded.Timelapse,
                            leadingText = getString(R.string.time)
                        )
                        if (area.isNotBlank()) IconText(
                            text = area,
                            leadingIcon = Icons.Rounded.LocationOn,
                            leadingText = getString(R.string.area)
                        )
                        IconText(
                            text = getString(campus.nameResId),
                            leadingIcon = Icons.Rounded.School,
                            leadingText = getString(R.string.campus)
                        )
                        IconText(
                            text = point,
                            leadingIcon = Icons.Rounded.Star,
                            leadingText = getString(R.string.point)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dp4(10))
                ) {
                    ElevatedButton(
                        onClick = {
                            if (course.selected.value) courseDeleteDialogVisible = true
                            else {
                                coroutineScope.launchCatch {
                                    select(operateId, sort, null)
                                    selected.value = true
                                    Toast(R.string.selected_).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.elevatedButtonColors(
                            contentColor = if (course.selected.value) MaterialTheme.colorScheme.error
                            else Color.Unspecified
                        ),
                        modifier = Modifier.padding(dp4(4))
                    ) {
                        Text(
                            text = getString(
                                if (course.selected.value) R.string.course_delete
                                else R.string.course_select
                            )
                        )
                    }
                }
            }

            CourseDeleteDialog(
                visible = courseDeleteDialogVisible,
                onDismiss = { courseDeleteDialogVisible = false },
                onConfirm = {
                    coroutineScope.launchCatch {
                        delete(operateId)
                        selected.value = false
                        courseDeleteDialogVisible = false
                        Toast(R.string.unselected).show()
                    }
                },
                courseName = name
            )
        }
    }
}


/**
 * 志愿选择
 * @param visible
 * @param onDismiss
 * @param onConfirm
 */
@Composable
private fun PriorityDialog(visible: Boolean, onDismiss: () -> Unit, onConfirm: (Priority) -> Unit) {
    DialogVisibility(visible = visible) {
        val priorities = getStringArray(R.array.priorities)
        var priority by remember { mutableIntStateOf(0) }

        Dialog(
            title = { Text(text = getString(R.string.priority)) },
            text = {
                priorities.forEachIndexed { index, item ->
                    RadioButton(
                        selected = priority == index,
                        onClick = { priority = index },
                        label = item
                    )
                }
            },
            onDismiss = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm(
                            when (priority) {
                                0 -> Priority.First
                                1 -> Priority.Second
                                else -> Priority.Third
                            }
                        )
                    }
                ) { Text(text = getString(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text(text = getString(R.string.cancel)) }
            },
            contentHorizontalAlignment = Alignment.Start
        )
    }
}


/**
 * 课程删除提示
 * @param visible
 * @param onDismiss
 * @param onConfirm
 * @param courseName 课程名称
 */
@Composable
private fun CourseDeleteDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    courseName: String?
) {
    DialogVisibility(visible = visible) {
        courseName?.let { name ->
            var value by remember { mutableStateOf("") }

            Dialog(
                text = {
                    SelectionContainer {
                        Text(text = getString(R.string.input_to_delete, name))
                    }
                    TextField(value = value, onValueChange = { value = it })
                },
                onDismiss = onDismiss,
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (value != name) Toast(R.string.input_does_not_match).show()
                            else onConfirm()
                        }
                    ) { Text(text = getString(R.string.confirm)) }
                },
                dismissButton = { TextButton(onClick = onDismiss) { Text(text = getString(R.string.cancel)) } }
            )
        }
    }
}


/**
 * 不可搜索课程数据源
 * @property courseSystem
 * @property sort
 */
private class UnsearchableCoursesPagingSource(
    private val courseSystem: CourseSystem,
    private val sort: Sort.Unsearchable
) : PagingSource<Course>() {
    override suspend fun loadCatching(params: LoadParams<Int>) =
        Page(courseSystem.list(sort, params.key!!), params)
}


/**
 * 可搜索课程数据源
 * @property courseSystem
 * @property sort
 * @property searchParams
 */
private class SearchableCoursesPagingSource(
    private val courseSystem: CourseSystem,
    private val sort: Sort.Searchable,
    private val searchParams: SearchParams
) : PagingSource<Course>() {
    override suspend fun loadCatching(params: LoadParams<Int>) =
        Page(courseSystem.search(sort, searchParams, params.key!!), params)
}