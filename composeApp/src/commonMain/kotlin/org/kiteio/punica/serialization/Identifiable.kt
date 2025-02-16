package org.kiteio.punica.serialization

/**
 * 可标识。
 *
 * @property id 唯一标识
 */
interface Identifiable<T> {
    val id: T
}