package org.kiteio.punica.ui.page.secondclass

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.foundation.User
import org.kiteio.punica.client.secondclass.SecondClass
import org.kiteio.punica.client.secondclass.api.SecondClassGrades
import org.kiteio.punica.client.secondclass.api.getGrades
import org.kiteio.punica.serialization.Stores
import org.kiteio.punica.serialization.get
import org.kiteio.punica.serialization.set
import org.kiteio.punica.wrapper.launchCatching

class SecondClassVM : ViewModel() {
    var secondClass by mutableStateOf<SecondClass?>(null)
        private set

    var grades by mutableStateOf<SecondClassGrades?>(null)
        private set

    var isGradesLoading by mutableStateOf(false)
        private set

    init {
        // 登录第二课堂
        viewModelScope.launchCatching {
            AppVM.userIdFlow.first()?.let { userId ->
                Stores.users.data.map { it.get<User>(userId) }.first()?.let { user ->
                    secondClass = SecondClass(user.id, user.secondClassPwd)
                }
            }
        }
    }


    /**
     * 更新成绩。
     */
    suspend fun updateGrades() {
        AppVM.userIdFlow.first()?.let { userId ->
            isGradesLoading = true
            // 本地获取
            grades = Stores.secondClassGrades.data.map {
                it.get<SecondClassGrades>(userId)
            }.first()?.also {
                isGradesLoading = false
            }

            try {
                secondClass?.run {
                    // 教务系统获取
                    grades = getGrades().also { grades ->
                        Stores.secondClassGrades.edit { it[grades.userId] = grades }
                    }
                }
            } finally {
                isGradesLoading = false
            }
        }
    }
}