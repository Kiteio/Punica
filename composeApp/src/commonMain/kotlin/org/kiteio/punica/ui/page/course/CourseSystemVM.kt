package org.kiteio.punica.ui.page.course

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.course.CourseSystem
import org.kiteio.punica.serialization.PrefsKeys
import org.kiteio.punica.serialization.Stores

class CourseSystemVM : ViewModel() {
    var courseSystem by mutableStateOf<CourseSystem?>(null)
        private set

    var isLoading by mutableStateOf(false)


    suspend fun updateCourseSystem() {
        AppVM.academicSystem?.run {
            isLoading = true
            try {
                val id = Stores.prefs.data.map { it[PrefsKeys.COURSE_SYSTEM_ID] }.first()
                courseSystem = CourseSystem(id).also { courseSystem ->
                    Stores.prefs.edit { it[PrefsKeys.COURSE_SYSTEM_ID] = courseSystem.id }
                }
            } finally {
                isLoading = false
            }
        }
    }
}