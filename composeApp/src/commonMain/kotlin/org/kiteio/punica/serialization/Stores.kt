package org.kiteio.punica.serialization

import androidx.datastore.preferences.core.PreferenceDataStoreFactory

/**
 * 存储。
 */
object Stores {
    /** 首选项 */
    val prefs by lazy { dataStore("prefs") }

    /** 用户 */
    val users by lazy { dataStore("users") }

    /** 课表 */
    val timetable by lazy { dataStore("timetable") }

    /** 成绩 */
    val grades by lazy { dataStore("grades") }
}


/**
 * 返回 DataStore。
 */
private fun dataStore(name: String) = PreferenceDataStoreFactory
    .createWithPath { fileDir("datastore/$name.preferences_pb") }