package org.kiteio.punica.ui.page.timetable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.api.Timetable
import org.kiteio.punica.client.academic.api.getTimetable
import org.kiteio.punica.client.academic.foundation.Term
import org.kiteio.punica.serialization.Stores
import org.kiteio.punica.serialization.get
import org.kiteio.punica.serialization.set

class TimetableVM : ViewModel() {
    /** 课表 */
    var timetable by mutableStateOf<Timetable?>(null)
        private set

    /** 课表是否在加载中 */
    var isLoading by mutableStateOf(false)
        private set

    /** 是否在课表底部显示备注 */
    var bottomNoteVisible by mutableStateOf(true)
        private set

    /** 学期 */
    var term by mutableStateOf(Term.current)
        private set


    /**
     * 更新课表。
     */
    suspend fun updateTimetable() {
        AppVM.userIdFlow.first()?.let { userId ->
            isLoading = true
            // 本地获取
            timetable = Stores.timetable.data.map {
                it.get<Timetable>("$userId$term")
            }.first()?.also {
                isLoading = false
            }

            try {
                AppVM.academicSystem?.run {
                    // 教务系统获取
                    timetable = getTimetable(term).also { timetable ->
                        // 本地保存
                        Stores.timetable.edit { it[timetable.id] = timetable }
                    }
                }
            } finally {
                isLoading = false
            }
        }
    }


    /**
     * 切换课表底部备注可见性。
     */
    fun switchBottomNoteVisible() {
        bottomNoteVisible = !bottomNoteVisible
    }


    /**
     * 改变学期。
     */
    fun changeTerm(value: Term) {
        term = value
    }
}