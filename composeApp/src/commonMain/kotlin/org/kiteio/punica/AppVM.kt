package org.kiteio.punica

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.*
import org.jetbrains.compose.resources.getString
import org.kiteio.punica.AppVM.academicSystem
import org.kiteio.punica.client.academic.AcademicSystem
import org.kiteio.punica.client.academic.api.getNetworkPwd
import org.kiteio.punica.client.academic.api.getTermDateRange
import org.kiteio.punica.client.academic.foundation.Campus.CANTON
import org.kiteio.punica.client.academic.foundation.Campus.FO_SHAN
import org.kiteio.punica.client.academic.foundation.User
import org.kiteio.punica.serialization.*
import org.kiteio.punica.ui.component.showToast
import org.kiteio.punica.ui.page.account.PasswordType
import org.kiteio.punica.ui.page.account.PasswordType.*
import org.kiteio.punica.ui.theme.ThemeMode
import org.kiteio.punica.ui.theme.ThemeMode.*
import org.kiteio.punica.wrapper.now
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.delete_successful

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
    val week = Stores.prefs.data.map { prefs ->
        val now = LocalDate.now()
        val date = prefs[PrefsKeys.TERM_START_DATE]?.let {
            LocalDate.parse(it)
        } ?: now
        date.run {
            (daysUntil(now) + (dayOfWeek.ordinal - now.dayOfWeek.ordinal)) / 7
        }.coerceIn(0..20)
    }

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
                Stores.users.data.map { it.get<User>(userId) }.first()?.let { user ->
                    // 设置第二课堂当前账号
                    if (secondClassUserId.first() == null) {
                        Stores.prefs.edit { prefs ->
                            prefs[PrefsKeys.SECOND_CLASS_USER_ID] = userId
                        }
                    }
                    // 设置校园网当前账号
                    if (networkUserId.first() == null) {
                        Stores.prefs.edit { prefs ->
                            prefs[PrefsKeys.NETWORK_USER_ID] = userId
                        }
                    }

                    // 登录
                    try {
                        academicSystem = AcademicSystem(user).apply {
                            // 获取并设置校园网密码
                            if (user.networkPwd.isEmpty()) {
                                val u = user.copy(networkPwd = getNetworkPwd())
                                Stores.users.edit { prefs ->
                                    prefs[u.id] = u
                                }
                            }
                            // 获取并设置开学日期
                            val old = Stores.prefs.data.map { prefs ->
                                prefs[PrefsKeys.TERM_START_DATE]?.let { LocalDate.parse(it) }
                            }.first()
                            val start = getTermDateRange().range.start.minus(DatePeriod(days = 7))
                            if (old == null || old.monthsUntil(start) >= 4) {
                                Stores.prefs.edit { it[PrefsKeys.TERM_START_DATE] = "$start" }
                            }
                        }
                    } catch (throwable: Throwable) {
                        // 验证码错误重试
                        if (throwable.message == "验证码错误!!") updateAcademicSystem(userId)
                        else throw throwable
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
    suspend fun deleteUser(type: PasswordType, userId: String) {
        if (type == OTP) {
            // 将 OTP 密钥清空
            Stores.users.edit {
                it[userId] = it.get<User>(userId)?.copy(otpSecret = "")
            }
        } else {
            val key = when (type) {
                Academic -> PrefsKeys.ACADEMIC_USER_ID
                SecondClass -> PrefsKeys.SECOND_CLASS_USER_ID
                Network -> PrefsKeys.NETWORK_USER_ID
                OTP -> error("")
            }
            // 删除数据
            Stores.timetable.edit { prefs -> prefs.removeAll { it.contains(userId) } }
            Stores.exams.edit { prefs -> prefs.removeAll { it == userId } }
            Stores.grades.edit { prefs -> prefs.removeAll { it == userId } }
            Stores.secondClassGrades.edit { prefs -> prefs.removeAll { it == userId } }
            Stores.plans.edit { prefs -> prefs.removeAll { it == userId } }
            Stores.progresses.edit { prefs -> prefs.removeAll { it == userId } }
            // 删除用户
            Stores.users.edit { it.remove(userId) }
            // 删除学号
            Stores.prefs.edit {
                if (it[key] == userId) {
                    it.remove(key)
                    // 退出教务系统
                    if (type == Academic) {
                        academicSystem = null
                    }
                }
            }
        }
        showToast(getString(Res.string.delete_successful))
    }


    /**
     * 改变当前周次。
     */
    suspend fun changeWeek(value: Int) {
        require(value in 0..<TIMETABLE_MAX_PAGE)
        Stores.prefs.edit { it[PrefsKeys.TERM_START_DATE] = "${LocalDate.now().minus(DatePeriod(days = 7 * value))}" }
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