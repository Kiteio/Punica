package org.kiteio.punica

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.monthsUntil
import org.jetbrains.compose.resources.getString
import org.kiteio.punica.AppVM.academicSystem
import org.kiteio.punica.client.academic.AcademicSystem
import org.kiteio.punica.client.academic.api.getTermDateRange
import org.kiteio.punica.client.academic.api.logout
import org.kiteio.punica.client.academic.foundation.Campus.CANTON
import org.kiteio.punica.client.academic.foundation.Campus.FO_SHAN
import org.kiteio.punica.client.academic.foundation.User
import org.kiteio.punica.serialization.PrefsKeys
import org.kiteio.punica.serialization.Stores
import org.kiteio.punica.serialization.get
import org.kiteio.punica.serialization.remove
import org.kiteio.punica.serialization.removeAll
import org.kiteio.punica.serialization.set
import org.kiteio.punica.ui.component.showToast
import org.kiteio.punica.ui.theme.ThemeMode
import org.kiteio.punica.ui.theme.ThemeMode.Dark
import org.kiteio.punica.ui.theme.ThemeMode.Default
import org.kiteio.punica.ui.theme.ThemeMode.Light
import org.kiteio.punica.wrapper.now
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.delete_successful

object AppVM : ViewModel() {
    /** 课表最大页数 */
    const val TIMETABLE_MAX_PAGE = 21

    /** 学号 */
    val userIdFlow = Stores.prefs.data.map { it[PrefsKeys.USER_ID] }

    /** 用户 */
    val userFlow = flow {
        emit(
            userIdFlow.first()?.let { userId ->
                Stores.users.data.map { it.get<User>(userId) }.first()
            }
        )
    }

    /** 教务系统 */
    var academicSystem by mutableStateOf<AcademicSystem?>(null)
        private set

    /** 是否正在登录 */
    var isLoggingIn by mutableStateOf(false)
        private set

    /** 周次 */
    val weekFlow = Stores.prefs.data.map { prefs ->
        val now = LocalDate.now()
        val date = prefs[PrefsKeys.TERM_START_DATE]?.let {
            LocalDate.parse(it)
        } ?: now
        date.run {
            (daysUntil(now) + (dayOfWeek.ordinal - now.dayOfWeek.ordinal)) / 7
        }.coerceIn(0..20)
    }

    /** 主题模式 */
    val themeModeFlow = Stores.prefs.data.map {
        when (it[PrefsKeys.THEME_MODE]) {
            Light.ordinal -> Light
            Dark.ordinal -> Dark
            else -> Default
        }
    }

    /** 校区 */
    val campusFlow = Stores.prefs.data.map {
        when (it[PrefsKeys.CAMPUS]) {
            FO_SHAN.ordinal -> FO_SHAN
            else -> CANTON
        }
    }


    suspend fun logout() {
        academicSystem?.logout()
        academicSystem = null
    }


    suspend fun login(user: User? = null, shouldSave: Boolean = false) {
        (user ?: userFlow.first())?.let { user ->
            try {
                // 登录
                isLoggingIn = true
                academicSystem = AcademicSystem(user).apply {
                    // 获取并设置开学日期
                    val old = Stores.prefs.data.map { prefs ->
                        prefs[PrefsKeys.TERM_START_DATE]?.let { LocalDate.parse(it) }
                    }.first()
                    val start = getTermDateRange().range.start.minus(DatePeriod(days = 7))
                    if (old == null || old.monthsUntil(start) >= 4) {
                        Stores.prefs.edit { it[PrefsKeys.TERM_START_DATE] = "$start" }
                    }
                }

                if (shouldSave) {
                    // 保存用户
                    Stores.users.edit {
                        it[user.id] = it.get<User>(user.id)?.copy(
                            password = user.password,
                            secondClassPwd = user.secondClassPwd,
                        ) ?: user
                    }
                    Stores.prefs.edit { it[PrefsKeys.USER_ID] = user.id }
                }
            } catch (throwable: Throwable) {
                // 验证码错误重试
                if (throwable.message == "验证码错误!!") login(user)
                else throw throwable
            } finally {
                isLoggingIn = false
            }
        }
    }


    /**
     * 更新 [academicSystem]。
     */
    suspend fun updateAcademicSystem(userId: String?) {
        if (userId != null) {
            // 防止同一账号重复登录
            if (academicSystem?.userId != userId) {
                userFlow.first()?.let { user ->
                    try {
                        // 登录
                        isLoggingIn = true
                        academicSystem = AcademicSystem(user).apply {
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
                    } finally {
                        isLoggingIn = false
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
    suspend fun deleteUser(userId: String) {
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
            if (it[PrefsKeys.USER_ID] == userId) {
                it.remove(PrefsKeys.USER_ID)
                // 退出教务系统
                if (academicSystem?.userId == userId) {
                    academicSystem = null
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
        Stores.prefs.edit {
            it[PrefsKeys.TERM_START_DATE] = "${LocalDate.now().minus(DatePeriod(days = 7 * value))}"
        }
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