package org.kiteio.punica.mirror.util

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * 返回当前的 [LocalDate]。
 */
fun LocalDate.Companion.now() = LocalDateTime.now().date

/**
 * 返回当前的 [LocalDateTime]。
 */
fun LocalDateTime.Companion.now() = Clock.System.now()
    .toLocalDateTime(TimeZone.currentSystemDefault())