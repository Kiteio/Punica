package org.kiteio.punica.mirror.ui.pages.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object TimetableRoute

fun NavGraphBuilder.timetableDestination() {
    composable<TimetableRoute> {

    }
}