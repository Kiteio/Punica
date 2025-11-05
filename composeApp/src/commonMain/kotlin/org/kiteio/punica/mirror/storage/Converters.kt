package org.kiteio.punica.mirror.storage

import androidx.room.TypeConverter
import io.ktor.http.*
import kotlinx.datetime.LocalDate
import org.kiteio.punica.mirror.util.Json

class Converters {
    // --------------- Cookies ---------------
    @TypeConverter
    fun fromCookies(cookies: List<Cookie>): String {
        return Json.encodeToString(cookies)
    }

    @TypeConverter
    fun toCookies(jsonString: String): List<Cookie> {
        return Json.decodeFromString<List<Cookie>>(jsonString)
    }

    // --------------- LocalDate ---------------
    @TypeConverter
    fun fromLocalDate(date: LocalDate) = "$date"

    @TypeConverter
    fun toLocalDate(jsonString: String) = LocalDate.parse(jsonString)
}