package org.kiteio.punica.serialization

import androidx.datastore.preferences.core.PreferenceDataStoreFactory

/**
 * 存储。
 */
object Stores {
    /** 用户 */
    val users = dataStore("users")
}


/**
 * 返回 DataStore。
 */
private fun dataStore(name: String) = PreferenceDataStoreFactory
    .createWithPath { fileDir("datastore/$name.preferences_pb") }