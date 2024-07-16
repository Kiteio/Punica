package org.kiteio.punica.ui.screen.module

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.kiteio.punica.R
import org.kiteio.punica.getString
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.navigation.Route

/**
 * 选课系统
 */
@Composable
fun CourseSystemScreen() {
    ScaffoldBox(topBar = { NavBackTopAppBar(route = Route.Module.CourseSystem) }) {
        Text(text = getString(R.string.course_system))
    }
}