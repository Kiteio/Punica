package org.kiteio.punica.mirror.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * 以同步方式收集第一个值。
 */
fun <T> Flow<T>.syncFirst(): T = runBlocking { first() }