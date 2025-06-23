package org.kiteio.punica.mirror.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalDateTime.Companion.Format
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
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

/**
 * 返回 [epochMilliseconds] 的 [LocalDateTime]。
 */
fun LocalDateTime.Companion.from(epochMilliseconds: Long): LocalDateTime {
    return Instant.fromEpochMilliseconds(epochMilliseconds)
        .toLocalDateTime(TimeZone.currentSystemDefault())
}

private val dateTimeWithoutSecondFormat by lazy {
    Format {
        year(); char('-')
        monthNumber();char('-')
        dayOfMonth(); char(' ')
        hour(); char(':')
        minute()
    }
}

/**
 * 返回“yyyy-MM-dd HH:mm” [input] 的 [LocalDateTime]。
 */
fun LocalDateTime.Companion.parseIsoVariantWithoutSecond(input: CharSequence): LocalDateTime =
    parse(input, dateTimeWithoutSecondFormat)

private val dateTimeFormat by lazy {
    Format {
        year(); char('-')
        monthNumber();char('-')
        dayOfMonth(); char(' ')
        hour(); char(':')
        minute(); char(':')
        second()
    }
}

/**
 * 返回“yyyy-MM-dd HH:mm:ss” [input] 的 [LocalDateTime]。
 */
fun LocalDateTime.Companion.parseIsoVariant(input: CharSequence): LocalDateTime =
    parse(input, dateTimeFormat)