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
    var isTimetableLoading by mutableStateOf(false)
        private set

    /** 是否在课表底部显示备注 */
    var isBottomNoteVisible by mutableStateOf(true)
        private set

    /** 学期 */
    var term by mutableStateOf(Term.current)
        private set


    /**
     * 切换课表。
     */
    suspend fun switchTimetable() {
        AppVM.academicSystem?.run {
            isTimetableLoading = true
            // 本地获取
            timetable = getTimetableFromStore()

            try {
                // 教务系统获取
                timetable = getTimetable(term).also { timetable ->
                    // 本地保存
                    Stores.timetable.edit { it[timetable.id] = timetable }
                }
            } finally {
                isTimetableLoading = false
            }
        }
    }


    private suspend fun getTimetableFromStore(): Timetable? {
        return AppVM.academicUserId.first()?.let { userId ->
            Stores.timetable.data.map { it.get<Timetable>("$userId$term") }
        }?.first()
    }


    /**
     * 切换课表底部备注可见性。
     */
    fun switchBottomNoteVisible() {
        isBottomNoteVisible = !isBottomNoteVisible
    }


    /**
     * 改变学期。
     */
    fun changeTerm(value: Term) {
        term = value
    }
}