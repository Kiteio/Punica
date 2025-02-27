package org.kiteio.punica.ui.page.grades

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.api.Grades
import org.kiteio.punica.client.academic.api.getGrades
import org.kiteio.punica.serialization.Stores
import org.kiteio.punica.serialization.get
import org.kiteio.punica.serialization.set

class GradesVM : ViewModel() {
    /** 成绩 */
    var grades by mutableStateOf<Grades?>(null)
        private set

    /** 成绩是否在加载中 */
    var isGradesLoading by mutableStateOf(false)
        private set


    /**
     * 切换成绩。
     */
    suspend fun updateGrades() {
        AppVM.academicUserId.first()?.let { userId ->
            isGradesLoading = true
            // 本地获取
            grades = Stores.grades.data.map {
                it.get<Grades>(userId)
            }.first()?.also {
                isGradesLoading = false
            }

            try {
                AppVM.academicSystem?.run {
                    // 教务系统获取
                    grades = getGrades().also { grades ->
                        Stores.grades.edit { it[grades.userId] = grades }
                    }
                }
            } finally {
                isGradesLoading = false
            }
        }
    }
}