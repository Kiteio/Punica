package org.kiteio.punica.serialization

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Preferences 键。
 */
object PrefsKeys {
    /** 周次 */
    val WEEK = intPreferencesKey("week")

    /** 主题模式 */
    val THEME_MODE = intPreferencesKey("theme_mode")

    /** 校区 */
    val CAMPUS = intPreferencesKey("CAMPUS")

    /** 教务系统学号 */
    val ACADEMIC_USER_ID = stringPreferencesKey("academic_user_id")

    /** 第二课堂学号 */
    val SECOND_CLASS_USER_ID = stringPreferencesKey("second_class_user_id")

    /** 校园网学号 */
    val NETWORK_USER_ID = stringPreferencesKey("network_user_id")

    /** 选课系统 id */
    val COURSE_SYSTEM_ID = stringPreferencesKey("course_system_id")
}