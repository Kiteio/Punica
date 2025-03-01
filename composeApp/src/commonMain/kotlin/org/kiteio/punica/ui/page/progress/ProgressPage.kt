package org.kiteio.punica.ui.page.progress

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.ChartLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.api.ProgressModule
import org.kiteio.punica.ui.page.modules.ModuleRoute
import org.kiteio.punica.ui.component.LoadingNotNullOrEmpty
import org.kiteio.punica.ui.component.NavBackAppBar
import org.kiteio.punica.wrapper.LaunchedEffectCatching
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.academic_progress

/**
 * 学业进度页面路由。
 */
@Serializable
object ProgressRoute : ModuleRoute {
    override val nameRes = Res.string.academic_progress
    override val icon = TablerIcons.ChartLine
}


/**
 * 学业进度页面。
 */
@Composable
fun ProgressPage() = viewModel { ProgressVM() }.Content()


@Composable
private fun ProgressVM.Content() {
    var moduleBottomSheetVisible by remember { mutableStateOf(false) }
    var module by remember { mutableStateOf<ProgressModule?>(null) }

    LaunchedEffectCatching(AppVM.academicSystem) {
        updateProgresses()
    }

    Scaffold(
        topBar = { NavBackAppBar(title = { Text(stringResource(ProgressRoute.nameRes)) }) },
    ) { innerPadding ->
        LoadingNotNullOrEmpty(
            progresses?.modules,
            isLoading = isLoading,
            modifier = Modifier.padding(innerPadding),
        ) { modules ->
            var innerModules by remember { mutableStateOf<List<ProgressModule>?>(null) }

            LaunchedEffectCatching(Unit) {
                withContext(Dispatchers.Default) {
                    val map = modules.flatMap { it.progresses }.groupBy { "${it.moduleName}  ${it.category}" }

                    val progressModules = mutableListOf<ProgressModule>()
                    map.forEach { (key, value) ->
                        progressModules.add(
                            ProgressModule(
                                key,
                                value.firstNotNullOfOrNull { it.requiredCredits },
                                value.sumOf { it.earnedCredits ?: 0.0 },
                                value,
                            )
                        )
                    }
                    innerModules = progressModules.sortedBy { it.moduleName }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(200.dp),
                contentPadding = PaddingValues(4.dp),
            ) {
                items(modules) {
                    Progress(
                        name = it.moduleName,
                        earnedCredits = it.earnedCredits,
                        requiredCredits = it.requiredCredits,
                        onClick = {
                            module = it
                            moduleBottomSheetVisible = true
                        },
                        modifier = Modifier.padding(4.dp),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                innerModules?.run {
                    items(this@run) {
                        Progress(
                            name = it.moduleName,
                            earnedCredits = it.earnedCredits,
                            onClick = {
                                module = it
                                moduleBottomSheetVisible = true
                            },
                            modifier = Modifier.padding(4.dp),
                            requiredCredits = it.requiredCredits,
                        )
                    }
                }
            }
        }
    }

    ModuleBottomSheet(
        moduleBottomSheetVisible,
        onDismissRequest = { moduleBottomSheetVisible = false },
        module = module,
    )
}


@Composable
private fun Progress(
    name: String,
    earnedCredits: Double?,
    requiredCredits: Double?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight? = null,
) {
    ElevatedCard(onClick = onClick, modifier = modifier) {
        ListItem(
            headlineContent = { Text(name, fontWeight = fontWeight) },
            trailingContent = {
                Text(
                    "${earnedCredits ?: ""} / ${requiredCredits ?: ""}",
                    color = if (
                        earnedCredits != null &&
                        requiredCredits != null &&
                        earnedCredits / requiredCredits < 1
                    ) MaterialTheme.colorScheme.error
                    else LocalContentColor.current,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
        )
    }
}