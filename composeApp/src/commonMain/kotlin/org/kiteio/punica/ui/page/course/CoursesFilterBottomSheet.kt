package org.kiteio.punica.ui.page.course

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.client.academic.foundation.Campus
import org.kiteio.punica.client.course.api.SearchParameters
import org.kiteio.punica.client.course.foundation.Section
import org.kiteio.punica.ui.widget.ModalBottomSheet
import org.kiteio.punica.wrapper.focusCleaner
import punica.composeapp.generated.resources.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CoursesFilterBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    parameters: SearchParameters,
    onParamsChange: (SearchParameters) -> Unit,
) {
    ModalBottomSheet(visible, onDismissRequest) {
        val focusManager = LocalFocusManager.current

        val daysOfWeek = stringArrayResource(Res.array.days_of_week)

        var name by remember { mutableStateOf(parameters.name) }
        var teacher by remember { mutableStateOf(parameters.teacher) }
        var dayOfWeek by remember { mutableStateOf(parameters.dayOfWeek) }
        var section by remember { mutableStateOf(parameters.section) }
        var campus by remember { mutableStateOf(parameters.campus) }

        Column(modifier = Modifier.focusCleaner(focusManager)) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxHeight(0.7f),
            ) {
                item {
                    // 课程名称
                    TextField(
                        name,
                        onValueChange = { name = it },
                        label = stringResource(Res.string.course),
                    )
                    // 教师
                    TextField(
                        teacher,
                        onValueChange = { teacher = it },
                        label = stringResource(Res.string.teacher),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    // 星期
                    Title(stringResource(Res.string.day_of_week))
                    FlowRow {
                        DayOfWeek.entries.forEachIndexed { index, it ->
                            FilterChip(
                                selected = dayOfWeek == it,
                                onClick = { dayOfWeek = if (dayOfWeek == it) null else it },
                                label = {
                                    Text("${stringResource(Res.string.day_of_week)}${daysOfWeek[index]}")
                                },
                                modifier = Modifier.padding(4.dp),
                                leadingIcon = if (dayOfWeek == it) {
                                    {
                                        Icon(
                                            Icons.Outlined.Check,
                                            contentDescription = stringResource(Res.string.checked),
                                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                                        )
                                    }
                                } else null,
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    // 节次
                    Title(stringResource(Res.string.sections))
                    FlowRow {
                        Section.entries.forEach {
                            FilterChip(
                                selected = section == it,
                                onClick = { section = if (section == it) null else it },
                                label = { Text("$it") },
                                modifier = Modifier.padding(4.dp),
                                leadingIcon = if (section == it) {
                                    {
                                        Icon(
                                            Icons.Outlined.Check,
                                            contentDescription = stringResource(Res.string.checked),
                                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                                        )
                                    }
                                } else null,
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    // 校区
                    Title(stringResource(Res.string.campus))
                    FlowRow {
                        Campus.entries.forEach {
                            FilterChip(
                                selected = campus == it,
                                onClick = { campus = if (campus == it) null else it },
                                label = { Text(stringResource(it.nameRes)) },
                                modifier = Modifier.padding(4.dp),
                                leadingIcon = if (campus == it) {
                                    {
                                        Icon(
                                            Icons.Outlined.Check,
                                            contentDescription = stringResource(Res.string.checked),
                                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                                        )
                                    }
                                } else null,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            Surface(shadowElevation = 1.dp, tonalElevation = 1.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    ElevatedButton(
                        onClick = {
                            name = ""
                            teacher = ""
                            dayOfWeek = null
                            section = null
                            campus = null
                        },
                    ) {
                        Text(stringResource(Res.string.clear))
                    }
                    Spacer(modifier = Modifier.width(32.dp))
                    ElevatedButton(
                        onClick = {
                            onParamsChange(
                                parameters.copy(
                                    name = name,
                                    teacher = teacher,
                                    dayOfWeek = dayOfWeek,
                                    section = section,
                                    campus = campus,
                                )
                            )
                        },
                    ) {
                        Text(stringResource(Res.string.confirm))
                    }
                }
            }
        }
    }
}


@Composable
private fun Title(text: String) {
    Text(
        text,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
    )
}


@Composable
private fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        trailingIcon = if (value.isNotBlank()) {
            {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = stringResource(Res.string.clear),
                    )
                }
            }
        } else null,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(0.6f),
        ),
    )
}