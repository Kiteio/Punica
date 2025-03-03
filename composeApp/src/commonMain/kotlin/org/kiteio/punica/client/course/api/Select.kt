package org.kiteio.punica.client.course.api

import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.client.course.CourseSystem
import org.kiteio.punica.client.course.foundation.CourseCategory
import org.kiteio.punica.client.course.foundation.CourseOperateBody
import org.kiteio.punica.serialization.Json

/**
 * 选择操作 id 为 [sCourseId] 的 [category] 类型的课程。
 * 若 [priority] 不为 null，则选定为 [priority] 指定的志愿。
 */
suspend fun CourseSystem.select(
    sCourseId: String,
    category: CourseCategory,
    priority: CoursePriority?,
) {
    return withContext(Dispatchers.Default) {
        val body = get("jsxsd/xsxkkc/${category.operate}xkOper") {
            parameter("jx0404id", sCourseId)
            parameter("xkzy", priority?.id ?: "")
            parameter("trjf", "")
            parameter("cxxdlx", "1")
        }.run { Json.decodeFromString<CourseOperateBody>(bodyAsText()) }

        require(body.isSuccess) { body.message }
    }
}


/**
 * 选课志愿。
 */
enum class CoursePriority {
    FIRST, SECOND, THIRD;
}


/**
 * 选课志愿 id。
 */
val CoursePriority.id get() = ordinal + 1