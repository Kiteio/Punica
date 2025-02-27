package org.kiteio.punica.ui.page.progress

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.api.Progresses
import org.kiteio.punica.client.academic.api.getProgresses
import org.kiteio.punica.serialization.Stores
import org.kiteio.punica.serialization.get
import org.kiteio.punica.serialization.set

class ProgressVM : ViewModel() {
    var progresses by mutableStateOf<Progresses?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set


    suspend fun updateProgresses() {
        AppVM.academicUserId.first()?.let { userId ->
            isLoading = true
            // 本地获取
            progresses = Stores.progresses.data.map {
                it.get<Progresses>(userId)
            }.first()?.also { isLoading = false }

            try {
                AppVM.academicSystem?.run {
                    // 教务系统获取
                    progresses = getProgresses().also { progresses ->
                        Stores.progresses.edit { it[progresses.userId] = progresses }
                    }
                }
            } finally {
                isLoading = false
            }
        }
    }
}