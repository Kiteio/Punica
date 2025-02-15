package org.kiteio.punica.client.course.foundation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 课程操作响应内容。
 *
 * @property isSuccess 是否成功
 * @property message 若不成功，则包含错误信息
 */
@Serializable
data class CourseOperateBody(
    @SerialName("success") val isSuccess: Boolean,
    val message: String = "",
)