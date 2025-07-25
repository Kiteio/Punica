package org.kiteio.punica.client.course.foundation

import org.jetbrains.compose.resources.StringResource
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.category_basic
import punica.composeapp.generated.resources.category_cross_grade
import punica.composeapp.generated.resources.category_cross_major
import punica.composeapp.generated.resources.category_elective
import punica.composeapp.generated.resources.category_general
import punica.composeapp.generated.resources.category_major

/**
 * 课程分类。
 *
 * @property search 搜索路由
 */
enum class CourseCategory(
    val search: String,
    val nameRes: StringResource,
) {
    /** 学科基础、专业必修课 */
    BASIC("Bx", Res.string.category_basic),

    /** 选修课 */
    OPTIONAL("Xx", Res.string.category_elective),

    /** 通识课 */
    GENERAL("Ggxxk", Res.string.category_general),

    /** 专业内计划课 */
    PROFESSIONAL("Bxqjh", Res.string.category_major),

    /** 跨年级 */
    CROSS_GRADE("Knj", Res.string.category_cross_grade),

    /** 跨专业 */
    INTERPROFESSIONAL("Faw", Res.string.category_cross_major);


    /** 操作路由 */
    val operate by lazy { search.lowercase() }
}