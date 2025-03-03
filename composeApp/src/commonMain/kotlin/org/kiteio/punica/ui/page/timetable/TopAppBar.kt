package org.kiteio.punica.ui.page.timetable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.foundation.Term
import punica.composeapp.generated.resources.*

/**
 * 课表顶部导航栏。
 *
 * @param week 周次
 * @param currentPage 当前页码
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableVM.TopAppBar(
    week: Int,
    currentPage: Int,
    onPageChange: (Int) -> Unit,
    onNoteDialogDisplayRequest: () -> Unit,
) {
    val userId by AppVM.academicUserId.collectAsState(null)

    TopAppBar(
        title = {
            Row {
                // 周次
                Week(
                    currentPage = currentPage,
                    onPageChange = onPageChange,
                    enabled = userId != null,
                )

                // 重置
                if (currentPage != week) {
                    IconButton(onClick = { onPageChange(week) }) {
                        Icon(
                            Icons.Outlined.Refresh,
                            contentDescription = stringResource(Res.string.reset),
                        )
                    }
                }
            }
        },
        actions = {
            // 备注
            IconButton(onClick = onNoteDialogDisplayRequest, enabled = timetable != null) {
                Icon(
                    Icons.Outlined.Info,
                    contentDescription = stringResource(Res.string.note),
                )
            }

            // 学期
            Term(userId = userId)
        }
    )
}


/**
 * 周次。
 */
@Composable
private fun Week(currentPage: Int, onPageChange: (Int) -> Unit, enabled: Boolean) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expanded = true }, enabled = enabled) {
            Text(
                if (currentPage == 0) stringResource(Res.string.all) else stringResource(Res.string.week_of, currentPage),
                style = MaterialTheme.typography.titleMedium,
            )
        }

        // 下拉菜单
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            repeat(AppVM.TIMETABLE_MAX_PAGE) {
                DropdownMenuItem(
                    text = {
                        Text(if (it == 0) stringResource(Res.string.all) else stringResource(Res.string.week_of, it))
                    },
                    onClick = {
                        onPageChange(it)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = if (it == currentPage) MaterialTheme.colorScheme.primary
                        else LocalContentColor.current,
                    ),
                )
            }
        }
    }
}


/**
 * 学期。
 */
@Composable
private fun TimetableVM.Term(userId: String?) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(
            onClick = { expanded = true },
            enabled = userId != null,
        ) {
            Text("$term", style = MaterialTheme.typography.titleMedium)
        }

        // 下拉菜单
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            userId?.let {
                val terms = Term.list(it, addition = true)

                for (item in terms) {
                    DropdownMenuItem(
                        text = { Text("$item") },
                        onClick = {
                            changeTerm(item)
                            expanded = false
                        },
                        colors = if (item == term) {
                            MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.primary,
                            )
                        } else MenuDefaults.itemColors(),
                    )
                }
            }
        }
    }
}