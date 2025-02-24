package org.kiteio.punica

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.kiteio.punica.client.academic.AcademicSystem
import org.kiteio.punica.client.academic.foundation.Campus.CANTON
import org.kiteio.punica.client.academic.foundation.Campus.FO_SHAN
import org.kiteio.punica.client.academic.foundation.User
import org.kiteio.punica.serialization.PrefsKeys
import org.kiteio.punica.serialization.Stores
import org.kiteio.punica.serialization.get
import org.kiteio.punica.serialization.remove
import org.kiteio.punica.ui.page.account.AccountCategory
import org.kiteio.punica.ui.page.account.AccountCategory.*
import org.kiteio.punica.ui.theme.ThemeMode
import org.kiteio.punica.ui.theme.ThemeMode.*

object AppVM : ViewModel() {
    /** 课表最大页数 */
    const val TIMETABLE_MAX_PAGE = 21

    /** 教务系统学号 */
    val academicUserId = Stores.prefs.data.map { it[PrefsKeys.ACADEMIC_USER_ID] }

    /** 第二课堂学号 */
    val secondClassUserId = Stores.prefs.data.map { it[PrefsKeys.SECOND_CLASS_USER_ID] }

    /** 校园网学号 */
    val networkUserId = Stores.prefs.data.map { it[PrefsKeys.NETWORK_USER_ID] }

    /** 教务系统 */
    var academicSystem by mutableStateOf<AcademicSystem?>(null)
        private set

    /** 周次 */
    val week = Stores.prefs.data.map { it[PrefsKeys.WEEK] ?: 1 }

    /** 主题模式 */
    val themeMode = Stores.prefs.data.map {
        when (it[PrefsKeys.THEME_MODE]) {
            Light.ordinal -> Light
            Dark.ordinal -> Dark
            else -> Default
        }
    }

    /** 校区 */
    val campus = Stores.prefs.data.map {
        when (it[PrefsKeys.CAMPUS]) {
            FO_SHAN.ordinal -> FO_SHAN
            else -> CANTON
        }
    }


    /**
     * 更新 [academicSystem]。
     */
    suspend fun updateAcademicSystem(userId: String?) {
        if (userId != null) {
            // 防止同一账号重复登录
            if (academicSystem?.userId != userId) {
                // 通过用户名获取用户
                Stores.users.data.map { it.get<User>(userId) }.first()?.let {
                    // 登录
                    try {
                        academicSystem = AcademicSystem(it)
                    } catch (e: Exception) {
                        // 验证码错误重试
                        if (e.message == "验证码错误!!") updateAcademicSystem(userId)
                        else throw e
                    }
                }
            }
        } else {
            // 登出
            academicSystem = null
        }
    }


    /**
     * 删除用户。
     */
    suspend fun deleteUser(category: AccountCategory, userId: String) {
        val key = when (category) {
            Academic -> PrefsKeys.ACADEMIC_USER_ID
            SecondClass -> PrefsKeys.SECOND_CLASS_USER_ID
            Network -> PrefsKeys.NETWORK_USER_ID
        }
        // 删除用户
        Stores.users.edit { it.remove(userId) }
        // 删除学号
        Stores.prefs.edit {
            if (it[key] == userId) {
                it.remove(key)
                // 退出教务系统
                if (category == Academic) {
                    academicSystem = null
                }
            }
        }
    }


    /**
     * 改变当前周次。
     */
    suspend fun changeWeek(value: Int) {
        require(value in 0..<TIMETABLE_MAX_PAGE)
        Stores.prefs.edit { it[PrefsKeys.WEEK] = value }
    }


    /**
     * 切换主题模式。
     */
    suspend fun switchTheme(isCurrentDarkTheme: Boolean) {
        Stores.prefs.edit {
            val ordinal = (if (isCurrentDarkTheme) Light else Dark).ordinal
            it[PrefsKeys.THEME_MODE] = ordinal
        }
    }


    /**
     * 改变主题模式。
     */
    suspend fun changeThemeMode(value: ThemeMode) {
        Stores.prefs.edit { it[PrefsKeys.THEME_MODE] = value.ordinal }
    }


    /**
     * 切换校区。
     */
    suspend fun switchCampus() {
        Stores.prefs.edit {
            val ordinal = when (it[PrefsKeys.CAMPUS]) {
                CANTON.ordinal -> FO_SHAN
                else -> CANTON
            }.ordinal
            it[PrefsKeys.CAMPUS] = ordinal
        }
    }
}