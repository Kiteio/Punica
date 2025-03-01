package org.kiteio.punica.ui.page.course

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.client.course.api.SearchParameters
import org.kiteio.punica.ui.component.Checkbox
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.filter
import punica.composeapp.generated.resources.filter_conflicts
import punica.composeapp.generated.resources.filter_full

/**
 * 顶部过滤器。
 */
@Composable
fun CoursesFilter(
    parameters: SearchParameters,
    onParamsChange: (SearchParameters) -> Unit,
    onOpenFilter: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row {
                // 只看有剩余
                Checkbox(
                    parameters.filterFull,
                    onCheckedChange = { onParamsChange(parameters.copy(filterFull = it)) },
                    label = { Text(stringResource(Res.string.filter_full)) },
                )
                Spacer(modifier = Modifier.width(8.dp))
                // 只看不冲突
                Checkbox(
                    parameters.filterConflicts,
                    onCheckedChange = { onParamsChange(parameters.copy(filterConflicts = it)) },
                    label = { Text(stringResource(Res.string.filter_conflicts)) },
                )
            }
            // 过滤
            IconToggleButton(
                checked = parameters.name.isNotBlank() ||
                        parameters.teacher.isNotBlank() ||
                        parameters.dayOfWeek != null ||
                        parameters.section != null ||
                        parameters.campus != null,
                onCheckedChange = { onOpenFilter() },
            ) {
                Icon(
                    Icons.Outlined.FilterAlt,
                    contentDescription = stringResource(Res.string.filter),
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
    }
}