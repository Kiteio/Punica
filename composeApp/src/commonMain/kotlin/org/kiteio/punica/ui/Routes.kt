package org.kiteio.punica.ui

import kotlinx.serialization.Serializable

/**
 * 导航路由。
 */
object Routes {
    /** 课表 */
    @Serializable
    object Timetable

    /** 模块 */
    @Serializable
    object Modules

    /** 设置 */
    @Serializable
    object Settings
}