package org.kiteio.punica.ui.page.exam

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.api.Exams
import org.kiteio.punica.client.academic.api.getExams
import org.kiteio.punica.serialization.Stores
import org.kiteio.punica.serialization.get
import org.kiteio.punica.serialization.set

class ExamVM : ViewModel() {
    var exams by mutableStateOf<Exams?>(null)
        private set

    var isLoading by mutableStateOf(false)


    suspend fun updateExams() {
        AppVM.userIdFlow.first()?.let { userId ->
            isLoading = true
            // 本地获取
            exams = Stores.exams.data.map {
                it.get<Exams>(userId)
            }.first()?.also { isLoading = true }

            try {
                AppVM.academicSystem?.run {
                    // 教务系统获取
                    exams = getExams().also { exams ->
                        // 本地保存
                        Stores.exams.edit { it[exams.userId] = exams }
                    }
                }
            } finally {
                isLoading = false
            }
        }
    }
}