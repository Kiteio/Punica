package org.kiteio.punica.mirror.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModuleBuilder

/**
 * 添加以 [NavKey] 为基类的 [serializer] 至多态序列器模块。
 */
inline fun <reified T : @Serializable NavKey> SerializersModuleBuilder.polymorphic(
    serializer: KSerializer<T>,
) {
    polymorphic(NavKey::class, T::class, serializer)
}