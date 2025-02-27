package org.kiteio.punica.ui.page.timetables

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.api.CourseTimetable
import org.kiteio.punica.client.academic.api.getCourseTimetable
import org.kiteio.punica.client.academic.foundation.Term
import org.kiteio.punica.serialization.Stores
import org.kiteio.punica.serialization.get
import org.kiteio.punica.serialization.removeAll
import org.kiteio.punica.serialization.set

class CourseTimetableVM : ViewModel() {
    var timetable by mutableStateOf<CourseTimetable?>(null)
        private set

    var isLoading by mutableStateOf(false)


    /**
     * 更新课程课表。
     */
    suspend fun updateTimetable() {
        isLoading = true
        // 本地获取
        timetable = Stores.courseTimetable.data.map {
            it.get<CourseTimetable>("${Term.current}")
        }.first()?.also {
            isLoading = false
        }

        try {
            AppVM.academicSystem?.run {
                // 教务系统获取
                timetable = getCourseTimetable(Term.current).also { courseTimetable ->
                    // 本地保存
                    Stores.courseTimetable.edit {
                        it["${courseTimetable.term}"] = courseTimetable
                        // 移除旧课表
                        it.removeAll { key -> key != "${Term.current}" }
                    }
                }
            }
        } finally {
            isLoading = false
        }
    }
}