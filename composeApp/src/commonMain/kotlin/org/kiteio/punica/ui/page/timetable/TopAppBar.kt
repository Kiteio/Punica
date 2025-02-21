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
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.note
import punica.composeapp.generated.resources.reset
import punica.composeapp.generated.resources.week

/**
 * 课表顶部导航栏。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableVM.TopAppBar(
    currentPage: Int,
    onPageChange: (Int) -> Unit,
    onNoteDialogDisplayRequest: () -> Unit,
) {
    TopAppBar(
        title = {
            Row {
                // 周次
                Week(
                    currentPage = currentPage,
                    onPageChange = onPageChange,
                    enabled = timetable != null,
                )

                // 重置
                if (currentPage != AppVM.week) {
                    IconButton(onClick = { onPageChange(AppVM.week) }) {
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
            IconButton(onClick = onNoteDialogDisplayRequest) {
                Icon(
                    Icons.Outlined.Info,
                    contentDescription = stringResource(Res.string.note),
                )
            }

            // 学期
            Term()
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
                stringResource(Res.string.week, currentPage),
                style = MaterialTheme.typography.titleMedium,
            )
        }

        // 下拉菜单
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            repeat(AppVM.TIMETABLE_MAX_PAGE) {
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.week, it)) },
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
private fun TimetableVM.Term() {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { if (AppVM.user != null) expanded = true }) {
            Text("$term", style = MaterialTheme.typography.titleMedium)
        }

        // 下拉菜单
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            AppVM.user?.id?.let {
                val terms = Term.list(it)

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