package org.kiteio.punica.client.course.foundation

/**
 * 课程分类。
 *
 * @property search 搜索路由
 */
enum class CourseCategory(val search: String) {
    /** 学科基础、专业必修课 */
    BASIC("Bx"),

    /** 选修课 */
    OPTIONAL("Xx"),

    /** 通识课 */
    GENERAL("Ggxxk"),

    /** 专业内计划课 */
    PROFESSIONAL("Bxqjh"),

    /** 跨年级 */
    CROSS_GRADE("Knj"),

    /** 跨专业 */
    INTERPROFESSIONAL("Faw");


    /** 操作路由 */
    val operate by lazy { search.lowercase() }
}