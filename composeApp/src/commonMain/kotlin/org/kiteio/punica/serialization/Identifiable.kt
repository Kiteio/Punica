package org.kiteio.punica.serialization

/**
 * 可标识
 */
interface Identifiable<T> {
    /** 唯一标识 */
    val id: T
}