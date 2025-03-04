package org.kiteio.punica.candy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

var LocalDateNow by mutableStateOf<LocalDate>(LocalDate.now())

var LocalDateTimeNow by mutableStateOf<LocalDateTime>(LocalDateTime.now())

/** 时间戳 */
val LocalDate.dateMillis
    get() = toEpochDay() * 24 * 60 * 60 * 1000L


/**
 * [LocalDate]
 * @param dateMillis
 * @return [LocalDate]
 */
fun LocalDate(dateMillis: Long): LocalDate =
    LocalDate.ofEpochDay(dateMillis / (24 * 60 * 60 * 1000L))


/**
 * [LocalDateTime]
 * @param dateMillis
 * @return [LocalDateTime]
 */
fun LocalDateTime(dateMillis: Long): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(dateMillis), ZoneId.systemDefault())


/**
 * [LocalDateTime] 格式化
 * @receiver [LocalDateTime]
 * @return [String]
 */
fun LocalDateTime.format(): String = format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))


/**
 * 返回两个 [LocalDate] 相差的天数
 * @receiver [LocalDate]
 * @param other
 * @return [Long]
 */
fun LocalDate.daysUntil(other: LocalDate) =
    ChronoUnit.DAYS.between(this, other)


/**
 * 返回两个 [LocalDateTime] 相差的分钟数
 * @receiver [LocalDateTime]
 * @param other
 * @return [Long]
 */
fun LocalDateTime.minutesUntil(other: LocalDateTime) =
    ChronoUnit.MINUTES.between(this, other)