package org.kiteio.punica.serialization

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 反序列化为列表。
 */
inline fun <reified T : Any> Flow<Preferences>.deserializeToList(): Flow<List<T>> {
    return map { preferences ->
        preferences.asMap().values.map {
            Json.decodeFromString<T>(it.toString())
        }.toList()
    }
}


/**
 * 将 [value] 序列化存入 [MutablePreferences]。
 */
inline operator fun <reified T> MutablePreferences.set(
    key: String,
    value: T,
) = set(stringPreferencesKey(key), Json.encodeToString(value))


/**
 * 使用 [key] 获取字符串并反序列化为对象。
 */
inline operator fun <reified T> Preferences.get(key: String) =
    get(stringPreferencesKey(key))?.let { Json.decodeFromString<T>(it) }


/**
 * 移除 [key]。
 */
fun MutablePreferences.remove(key: String) = remove(stringPreferencesKey(key))


/**
 * 移除符合 [predicate] 的 key。
 */
fun MutablePreferences.removeAll(predicate: (String) -> Boolean) =
    asMap().keys.map { it.name }.filter(predicate).onEach { remove(it) }