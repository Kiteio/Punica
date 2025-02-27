package org.kiteio.punica.ui.page.plan

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.api.Plans
import org.kiteio.punica.client.academic.api.getPlans
import org.kiteio.punica.serialization.Stores
import org.kiteio.punica.serialization.get
import org.kiteio.punica.serialization.set

class PlanVM : ViewModel() {
    var plans by mutableStateOf<Plans?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set


    suspend fun updatePlans() {
        AppVM.academicUserId.first()?.let { userId ->
            isLoading = true
            // 本地获取
            plans = Stores.plans.data.map {
                it.get<Plans>(userId)
            }.first()?.also { isLoading = false }

            try {
                AppVM.academicSystem?.run {
                    // 教务系统获取
                    plans = getPlans().also { plans ->
                        Stores.plans.edit { it[plans.userId] = plans }
                    }
                }
            } finally {
                isLoading = false
            }
        }
    }
}