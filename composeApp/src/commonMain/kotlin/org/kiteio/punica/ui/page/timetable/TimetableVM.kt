package org.kiteio.punica.ui.page.timetable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.api.Timetable
import org.kiteio.punica.client.academic.api.getTimetable
import org.kiteio.punica.client.academic.foundation.Term
import org.kiteio.punica.ui.widget.showToast

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
            try {
                timetable = getTimetable(term)
            } catch (e: Exception) {
                showToast(e)
            } finally {
                isTimetableLoading = false
            }
        }
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