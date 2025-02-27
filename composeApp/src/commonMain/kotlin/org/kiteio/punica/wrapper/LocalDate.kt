package org.kiteio.punica.wrapper

import kotlinx.datetime.*
import kotlinx.datetime.format.char

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


/**
 * yyyy-MM-dd HH:MM。
 */
private val localDateTimeFormat = LocalDateTime.Format {
    year(); char('-')
    monthNumber();char('-')
    dayOfMonth(); char(' ')
    hour(); char(':')
    minute()
}


/**
 * 时间戳转化为“yyyy-MM-dd HH:MM”。
 */
fun timestampToString(timestamp: Long) = Instant.fromEpochMilliseconds(timestamp)
    .toLocalDateTime(TimeZone.currentSystemDefault()).format(localDateTimeFormat )


/**
 * 转化为“yyyy-MM-dd HH:MM”。
 */
fun LocalDateTime.format() = format(localDateTimeFormat)