package org.kiteio.punica.wrapper

import kotlinx.datetime.*

/**
 * 返回当前时间对应的 [LocalDate]。
 */
fun LocalDate.Companion.now() =
    LocalDateTime.now().date


/**
 * 返回当前时间对应的 [LocalDateTime]。
 */
fun LocalDateTime.Companion.now() =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
