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

    /** 考试安排 */
    val exams by lazy { dataStore("exams") }

    /** 成绩 */
    val grades by lazy { dataStore("grades") }

    /** 第二课堂成绩 */
    val secondClassGrades by lazy { dataStore("second_class_grades") }

    /** 课程课表 */
    val courseTimetable by lazy { dataStore("course_timetable") }

    /** 执行计划 */
    val plans by lazy { dataStore("plans") }

    /** 学业进度 */
    val progresses by lazy { dataStore("progresses") }
}


/**
 * 返回 DataStore。
 */
private fun dataStore(name: String) = PreferenceDataStoreFactory
    .createWithPath { fileDir("datastore/$name.preferences_pb") }