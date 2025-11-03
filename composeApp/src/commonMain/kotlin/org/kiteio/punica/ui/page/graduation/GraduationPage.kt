package org.kiteio.punica.ui.page.graduation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.api.Graduation
import org.kiteio.punica.client.academic.api.downloadGraduationReport
import org.kiteio.punica.ui.component.CardListItem
import org.kiteio.punica.ui.component.LoadingNotNullOrEmpty
import org.kiteio.punica.ui.component.NavBackAppBar
import org.kiteio.punica.ui.component.showToast
import org.kiteio.punica.ui.page.modules.ModuleRoute
import org.kiteio.punica.ui.theme.PunicaTheme
import org.kiteio.punica.wrapper.LaunchedEffectCatching
import org.kiteio.punica.wrapper.launchCatching
import punica.composeapp.generated.resources.*

fun NavGraphBuilder.graduationDestination() {
    composable<GraduationRoute> { GraduationPage() }
}

@Serializable
object GraduationRoute : ModuleRoute {
    override val nameRes = Res.string.graduation
    override val icon = Icons.Outlined.School
}

@Composable
fun GraduationPage() {
    val viewModel = viewModel { GraduationVM() }

    LaunchedEffectCatching(Unit) {
        viewModel.updateGraduation()
    }

    Scaffold(
        topBar = {
            NavBackAppBar(
                title = { Text(stringResource(GraduationRoute.nameRes)) },
            )
        },
    ) { innerPadding ->
        LoadingNotNullOrEmpty(
            viewModel.graduation,
            isLoading = viewModel.isGraduationLoading,
            modifier = Modifier.padding(innerPadding),
        ) {
            GraduationPage(it)
        }
    }
}

@Preview
@Composable
private fun GraduationPagePreview() {
    PunicaTheme {
        Scaffold {
            GraduationPage(
                graduation = Graduation(
                    year = 2025,
                    name = "2025届主修",
                    type = "主修",
                    method = "系统报名",
                    credits = 2.0,
                    completionRate = 80,
                    enrolmentRate = 0,
                    docUrl = "url",
                )
            )
        }
    }
}

@Composable
private fun GraduationPage(graduation: Graduation) {
    val scope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(140.dp),
            contentPadding = PaddingValues(8.dp),
        ) {
            card(Res.string.graduation_year, "${graduation.year}")
            card(Res.string.graduation_name, graduation.name)
            card(Res.string.graduation_type, graduation.type)
            card(Res.string.enrolment_method, graduation.method)
            card(Res.string.graduation_credits, "${graduation.credits}")
            card(Res.string.graduation_completion_rate, "${graduation.completionRate}")
            card(Res.string.graduation_enrolment_rate, "${graduation.enrolmentRate}")
            graduation.note?.let {
                card(Res.string.note, it)
            }
        }
        ElevatedButton(
            onClick = {
                scope.launchCatching {
                    val path = AppVM.academicSystem?.downloadGraduationReport(graduation.docUrl)
                    showToast(path.toString())
                }
            }
        ) {
            Text(stringResource(Res.string.check_graduation_report))
        }
    }
}


private fun LazyGridScope.card(keyRes: StringResource, value: String) {
    item {
        CardListItem(
            headlineContent = { Text(stringResource(keyRes)) },
            supportingContent = { Text(value) },
            modifier = Modifier.padding(8.dp),
        )
    }
}