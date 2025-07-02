package org.kiteio.punica.mirror.modal.education

import kotlinx.datetime.LocalDateTime

/**
 * 选课系统。
 *
 * @property token 选课系统 id，每一轮会有一个不同的 id
 * @property name 轮次名称
 * @property startTime 开始时间
 * @property endTime 结束时间
 */
data class CourseSystem(
    val token: String,
    val name: String? = null,
    val startTime: LocalDateTime? = null,
    val endTime: LocalDateTime? = null,
)