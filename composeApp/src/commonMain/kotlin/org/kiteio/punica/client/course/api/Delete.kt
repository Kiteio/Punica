package org.kiteio.punica.client.course.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import org.kiteio.punica.client.course.CourseSystem
import org.kiteio.punica.client.course.foundation.CourseOperateBody

/**
 * 退掉操作 id 为 [sCourseId] 的课程。
 */
suspend fun CourseSystem.delete(sCourseId: String) {
    val body = get("jsxsd/xsxkjg/xstkOper") {
        parameter("jx0404id", sCourseId)
    }.body<CourseOperateBody>()

    require(body.isSuccess) { body.message }
}