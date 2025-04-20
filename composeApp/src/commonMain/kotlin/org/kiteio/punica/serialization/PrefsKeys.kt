package org.kiteio.punica.serialization

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Preferences 键。
 */
object PrefsKeys {
    /** 首次启动 */
    val FIRST_START = booleanPreferencesKey("first_start")

    /** 开学日期（第 0 周） */
    val TERM_START_DATE = stringPreferencesKey("term_start_date")

    /** 主题模式 */
    val THEME_MODE = intPreferencesKey("theme_mode")

    /** 校区 */
    val CAMPUS = intPreferencesKey("CAMPUS")

    /** 学号 */
    val USER_ID = stringPreferencesKey("user_id")

    /** 选课系统 id */
    val COURSE_SYSTEM_ID = stringPreferencesKey("course_system_id")
}