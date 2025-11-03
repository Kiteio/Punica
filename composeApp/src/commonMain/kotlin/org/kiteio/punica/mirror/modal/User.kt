package org.kiteio.punica.mirror.modal

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import io.ktor.http.*
import org.kiteio.punica.mirror.util.Json

/**
 * 用户。
 *
 * @property id 学号
 * @property password 门户密码
 * @property secondClassPwd 第二课堂密码
 */
@Entity(tableName = "user")
@TypeConverters(CookieConverter::class)
data class User(
    @PrimaryKey val id: String,
    val password: String,
    val secondClassPwd: String,
    val cookies: List<Cookie>,
)

/**
 * TOTP 用户。
 *
 * @property name 名称
 * @property secret 密钥
 */
data class TotpUser(
    val name: String,
    val secret: String,
)

class CookieConverter {
    @TypeConverter
    fun fromCookies(cookies: List<Cookie>): String {
        return Json.encodeToString(cookies)
    }

    @TypeConverter
    fun toCookies(jsonString: String): List<Cookie> {
        return Json.decodeFromString<List<Cookie>>(jsonString)
    }
}