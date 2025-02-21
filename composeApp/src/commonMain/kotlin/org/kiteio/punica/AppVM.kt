package org.kiteio.punica

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.kiteio.punica.client.academic.AcademicSystem
import org.kiteio.punica.client.academic.foundation.Campus
import org.kiteio.punica.client.academic.foundation.User
import org.kiteio.punica.ui.theme.ThemeMode
import org.kiteio.punica.ui.theme.ThemeMode.*
import org.kiteio.punica.ui.widget.showToast

object AppVM : ViewModel() {
    /** 课表最大页数 */
    const val TIMETABLE_MAX_PAGE = 21

    /** 用户 */
    var user by mutableStateOf<User?>(null)
        private set

    /** 教务系统 */
    var academicSystem by mutableStateOf<AcademicSystem?>(null)
        private set

    /** 当前周次 */
    var week by mutableIntStateOf(1)
        private set

    /** 主题模式 */
    var themeMode by mutableStateOf(Default)
        private set

    /** 校区 */
    var campus by mutableStateOf(Campus.CANTON)
        private set


    init {
        // 登录教务系统
        login()
    }


    /**
     * 登录教务系统。
     */
    private fun login() {
        user?.let {
            viewModelScope.launch {
                try {
                    academicSystem = AcademicSystem(it)
                } catch (e: Exception) {
                    if (e.message == "验证码错误!!") {
                        login()
                    } else showToast(e)
                }
            }
        }
    }


    /**
     * 改变当前周次。
     */
    fun changeWeek(value: Int) {
        require(week in 0..<TIMETABLE_MAX_PAGE)
        week = value
    }


    /**
     * 主题模式是否为暗色。
     */
    @Composable
    fun isDarkTheme() = when (themeMode) {
        Default -> isSystemInDarkTheme()
        Light -> false
        Dark -> true
    }


    /**
     * 切换主题模式。
     */
    fun switchTheme(isDarkTheme: Boolean) {
        themeMode = if (isDarkTheme) Light else Dark
    }


    /**
     * 改变主题模式。
     */
    fun changeThemeMode(value: ThemeMode) {
        themeMode = value
    }


    /**
     * 切换校区。
     */
    fun switchCampus() {
        campus = when (campus) {
            Campus.CANTON -> Campus.FO_SHAN
            Campus.FO_SHAN -> Campus.CANTON
        }
    }
}