package org.kiteio.punica.mirror.modal.secondclass

import kotlinx.datetime.LocalDateTime

/**
 * 活动。
 *
 * @property id 唯一标识
 * @property name 活动名称
 * @property category 分类
 * @property score 分数
 * @property duration 持续时间
 * @property organization 组织
 * @property logoUrl Logo Url
 * @property type 类型
 * @property isOnline 是否为线上
 */
data class BasicActivity(
    val id: Int,
    val name: String,
    val category: String,
    val score: Double,
    val duration: ClosedRange<LocalDateTime>,
    val organization: String,
    val logoUrl: String,
    val type: String,
    val isOnline: Boolean,
) {
    /**
     * 我的活动类型。
     */
    sealed class State(val value: Int) {
        /** 已报名 */
        data object Enrolled: State(0)
        /** 待开始 */
        data object PendingStart: State(1)
        /** 进行中 */
        data object Toward: State(2)
        /** 已完结 */
        data object Finished: State(3)
    }
}

/**
 * 活动详情。
 *
 * @property name 活动名称
 * @property description 描述
 * @property category 分类
 * @property score 分数
 * @property area 地点
 * @property deadline 报名截止时间
 * @property cover 封面
 * @property host 主办方
 * @property admin 管理员
 * @property phoneNumber 手机号
 * @property teacher 指导老师
 * @property trainingHours 培训时间
 * @property duration 持续时间
 * @property needSubmit 是否必须提交作业
 * @property total 最大人数
 * @property leftover 当前人数
 * @property type 类型
 */
data class Activity(
    val name: String,
    val description: String,
    val category: String,
    val score: Double,
    val area: String,
    val deadline: LocalDateTime,
    val cover: String,
    val host: String,
    val admin: String,
    val phoneNumber: String,
    val teacher: String?,
    val trainingHours: Double,
    val duration: ClosedRange<LocalDateTime>,
    val needSubmit: Boolean,
    val total: Int,
    val leftover: Int,
    val type: String,
)