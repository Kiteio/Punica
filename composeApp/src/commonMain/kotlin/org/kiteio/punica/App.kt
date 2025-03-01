package org.kiteio.punica

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.datastore.preferences.core.edit
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.serialization.PrefsKeys
import org.kiteio.punica.serialization.Stores
import org.kiteio.punica.ui.page.account.AccountPage
import org.kiteio.punica.ui.page.account.AccountRoute
import org.kiteio.punica.ui.page.call.EmergencyCallPage
import org.kiteio.punica.ui.page.call.EmergencyCallRoute
import org.kiteio.punica.ui.page.cet.CETPage
import org.kiteio.punica.ui.page.cet.CETRoute
import org.kiteio.punica.ui.page.course.CourseSystemPage
import org.kiteio.punica.ui.page.course.CourseSystemRoute
import org.kiteio.punica.ui.page.exam.ExamPage
import org.kiteio.punica.ui.page.exam.ExamRoute
import org.kiteio.punica.ui.page.grades.GradesPage
import org.kiteio.punica.ui.page.grades.GradesRoute
import org.kiteio.punica.ui.page.home.HomePage
import org.kiteio.punica.ui.page.home.HomeRoute
import org.kiteio.punica.ui.page.notice.NoticePage
import org.kiteio.punica.ui.page.notice.NoticeRoute
import org.kiteio.punica.ui.page.plan.PlanPage
import org.kiteio.punica.ui.page.plan.PlanRoute
import org.kiteio.punica.ui.page.progress.ProgressPage
import org.kiteio.punica.ui.page.progress.ProgressRoute
import org.kiteio.punica.ui.page.secondclass.SecondClassPage
import org.kiteio.punica.ui.page.secondclass.SecondClassRoute
import org.kiteio.punica.ui.page.teacher.TeacherProfilePage
import org.kiteio.punica.ui.page.teacher.TeacherProfileRoute
import org.kiteio.punica.ui.page.timetables.CourseTimetablePage
import org.kiteio.punica.ui.page.timetables.CourseTimetableRoute
import org.kiteio.punica.ui.page.websites.WebsitesPage
import org.kiteio.punica.ui.page.websites.WebsitesRoute
import org.kiteio.punica.ui.theme.PunicaTheme
import org.kiteio.punica.wrapper.LaunchedEffectCatching
import org.kiteio.punica.wrapper.launchCatching
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.close
import punica.composeapp.generated.resources.intro
import punica.composeapp.generated.resources.welcome

@Composable
fun App(windowSizeClass: WindowSizeClass, snackbarHostState: SnackbarHostState? = null) {
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val themeMode by AppVM.themeMode.collectAsState(
        runBlocking { AppVM.themeMode.first() }
    )
    val userId by AppVM.academicUserId.collectAsState(null)

    // 监听教务系统学号变化，更新教务系统
    LaunchedEffectCatching(userId) { AppVM.updateAcademicSystem(userId) }

    var firstStartDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffectCatching(Unit) {
        val isFirstStart = Stores.prefs.data.map { it[PrefsKeys.FIRST_START] != false }.first()
        firstStartDialogVisible = isFirstStart
    }

    Scaffold(
        snackbarHost = { if (snackbarHostState != null) SnackbarHost(hostState = snackbarHostState) },
    ) {
        PunicaTheme(
            themeMode = themeMode,
            windowSizeClass = windowSizeClass,
            navController = navController,
        ) {
            NavHost(
                navController = navController,
                startDestination = HomeRoute,
            ) {
                composable<HomeRoute> { HomePage() }
                composable<AccountRoute> { AccountPage(it.toRoute<AccountRoute>()) }
                composable<EmergencyCallRoute> { EmergencyCallPage() }
                composable<NoticeRoute> { NoticePage() }
                composable<WebsitesRoute> { WebsitesPage() }
                composable<CourseSystemRoute> { CourseSystemPage() }
                composable<ExamRoute> { ExamPage() }
                composable<CETRoute> { CETPage() }
                composable<GradesRoute> { GradesPage() }
                composable<SecondClassRoute> { SecondClassPage() }
                composable<TeacherProfileRoute> { TeacherProfilePage() }
                composable<CourseTimetableRoute> { CourseTimetablePage() }
                composable<PlanRoute> { PlanPage() }
                composable<ProgressRoute> { ProgressPage() }
            }
        }
    }

    FirstStartDialog(
        firstStartDialogVisible,
        onDismissRequest = {
            scope.launchCatching {
                Stores.prefs.edit { it[PrefsKeys.FIRST_START] = false }
            }
            firstStartDialogVisible = false
        },
    )
}


@Composable
private fun FirstStartDialog(visible: Boolean, onDismissRequest: () -> Unit) {
    if (visible) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(Res.string.close))
                }
            },
            title = {
                Text(stringResource(Res.string.welcome))
            },
            text = {
                LazyColumn {
                    item {
                        Text(stringResource(Res.string.intro))
                    }
                }
            },
        )
    }
}