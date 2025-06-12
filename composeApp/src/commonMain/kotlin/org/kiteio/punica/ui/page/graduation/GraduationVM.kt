package org.kiteio.punica.ui.page.graduation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.first
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.api.Graduation
import org.kiteio.punica.client.academic.api.getGraduation

class GraduationVM : ViewModel() {
    var graduation by mutableStateOf<Graduation?>(null)
        private set

    var isGraduationLoading by mutableStateOf(false)
        private set

    suspend fun updateGraduation() {
        AppVM.userIdFlow.first()?.let { userId ->
            isGraduationLoading = true
            try {
                AppVM.academicSystem?.run {
                    graduation = getGraduation()
                }
            } finally {
                isGraduationLoading = false
            }
        }
    }
}