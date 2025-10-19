package org.kiteio.punica.mirror.ui.screen.timetable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.kiteio.punica.mirror.ui.navigation.BottomNavKey
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.timetable

fun EntryProviderBuilder<NavKey>.timetableEntry() {
    entry<TimetableRoute> { TimetableScreen() }
}

@Serializable
data object TimetableRoute : BottomNavKey {
    override val strRes = Res.string.timetable
    override val icon = Icons.Outlined.CalendarMonth
    override val selectedIcon = Icons.Filled.CalendarMonth
}

@Composable
private fun TimetableScreen() {

}