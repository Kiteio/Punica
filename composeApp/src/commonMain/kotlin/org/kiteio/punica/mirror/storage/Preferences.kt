package org.kiteio.punica.mirror.storage

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath
import org.kiteio.punica.mirror.modal.Gender
import org.kiteio.punica.mirror.modal.education.Campus
import org.kiteio.punica.mirror.ui.theme.ThemeMode
import org.kiteio.punica.mirror.ui.theme.punicaColor
import org.kiteio.punica.mirror.util.AppDirs

/**
 * 首选项。
 */
object Preferences {
    /** DataStore */
    private val preferences by lazy {
        PreferenceDataStoreFactory
            .createWithPath {
                AppDirs.filesDir("datastore/preferences.preferences_pb").toPath()
            }
    }

    /**
     * 存储键。
     */
    private object Keys {
        /** 校区 */
        val campus = intPreferencesKey("campus")

        /** 周次 */
        val week = intPreferencesKey("week")

        /** 主色调 */
        val primaryColor = intPreferencesKey("primary_color")

        /** 性别 */
        val gender = intPreferencesKey("gender")

        /** 主题模式 */
        val themeMode = intPreferencesKey("theme_mode")
    }

    /** 校区 */
    val campus = preferences.data
        .map { it[Keys.campus] ?: Campus.Default.id }
        .map { Campus.getById(it) }

    /** 周次 */
    val week = preferences.data
        .map { it[Keys.week] ?: 0 }

    /** 主色调 */
    val primaryColor = preferences.data
        .map { it[Keys.primaryColor] ?: punicaColor.toArgb() }
        .map { Color(it) }

    /** 性别 */
    val gender = preferences.data
        .map { it[Keys.gender] ?: Gender.Default.ordinal }
        .map { Gender.entries[it] }

    /** 主题模式 */
    val themeMode = preferences.data
        .map { it[Keys.themeMode] ?: ThemeMode.Default.ordinal }
        .map { ThemeMode.entries[it] }

    /**
     * 更改校区。
     */
    suspend fun changeCampus(campus: Campus) {
        preferences.edit { it[Keys.campus] = campus.id }
    }

    /**
     * 更改周次。
     */
    suspend fun changeWeek(week: Int) {
        require(week in 0..20)

        preferences.edit { it[Keys.week] = week }
    }

    /**
     * 更改主色调。
     */
    suspend fun changePrimaryColor(color: Color) {
        preferences.edit {
            println(color)
            println(Color(color.toArgb()))
            it[Keys.primaryColor] = color.toArgb()
        }
    }

    /**
     * 更改性别。
     */
    suspend fun changeGender(gender: Gender) {
        preferences.edit { it[Keys.gender] = gender.ordinal }
    }

    /**
     * 更改主题模式。
     */
    suspend fun changeTheme(themeMode: ThemeMode) {
        preferences.edit { it[Keys.themeMode] = themeMode.ordinal }
    }
}