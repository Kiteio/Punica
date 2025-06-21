package org.kiteio.punica.mirror.modal.education

/**
 * 教师列表。
 *
 * @property pageCount 总页数
 * @property teachers 教师列表
 */
data class Teachers(
    val pageCount: Int,
    val teachers: List<BasicTeacher>,
)

/**
 * 教师基础信息。
 *
 * @property id 工号
 * @property name 姓名
 * @property factorial 院系
 */
data class BasicTeacher(
    val id: String,
    val name: String,
    val factorial: String?,
)

/**
 * 教师信息。
 *
 * @property name 姓名
 * @property gender 性别
 * @property politics 政治面貌
 * @property nation 民族
 * @property duty 职务
 * @property title 职称
 * @property category 教职工类别
 * @property factorial 部门（院系）
 * @property office 科室（系）
 * @property qualification 最高学历
 * @property degree 学位
 * @property field 研究方向
 * @property phoneNumber 手机号
 * @property qq QQ
 * @property weChat 微信
 * @property email 邮箱
 * @property biography 个人简介
 * @property taught 近四个学期主讲课程
 * @property teaching 下学期计划开设课程
 * @property philosophy 教育理念
 * @property slogan 最想对学生说的话
 */
data class Teacher(
    val id: String,
    val name: String,
    val gender: String?,
    val politics: String?,
    val nation: String?,
    val duty: String?,
    val title: String?,
    val category: String?,
    val factorial: String?,
    val office: String?,
    val qualification: String?,
    val degree: String?,
    val field: String?,
    val phoneNumber: String?,
    val qq: String?,
    val weChat: String?,
    val email: String?,
    val biography: String?,
    val taught: List<SimpleCourse>,
    val teaching: List<SimpleCourse>,
    val philosophy: String?,
    val slogan: String?,
) {
    /**
     * 教师信息 Builder。
     *
     * @property name 姓名
     * @property gender 性别
     * @property politics 政治面貌
     * @property nation 民族
     * @property duty 职务
     * @property title 职称
     * @property category 教职工类别
     * @property factorial 部门（院系）
     * @property office 科室（系）
     * @property qualification 最高学历
     * @property degree 学位
     * @property field 研究方向
     * @property phoneNumber 手机号
     * @property qq QQ
     * @property weChat 微信
     * @property email 邮箱
     * @property biography 个人简介
     * @property taught 近四个学期主讲课程
     * @property teaching 下学期计划开设课程
     * @property philosophy 教育理念
     * @property slogan 最想对学生说的话
     */
    class Builder {
        var id: String? = null
        var name: String? = null
        var gender: String? = null
        var politics: String? = null
        var nation: String? = null
        var duty: String? = null
        var title: String? = null
        var category: String? = null
        var factorial: String? = null
        var office: String? = null
        var qualification: String? = null
        var degree: String? = null
        var field: String? = null
        var phoneNumber: String? = null
        var qq: String? = null
        var weChat: String? = null
        var email: String? = null
        var biography: String? = null
        val taught = mutableListOf<SimpleCourse>()
        val teaching = mutableListOf<SimpleCourse>()
        var philosophy: String? = null
        var slogan: String? = null

        fun build(): Teacher {
            require(id != null)
            require(name != null)

            return Teacher(
                id = id!!,
                name = name!!,
                gender = gender,
                politics = politics,
                nation = nation,
                duty = duty,
                title = title,
                category = category,
                factorial = factorial,
                office = office,
                qualification = qualification,
                degree = degree,
                field = field,
                phoneNumber = phoneNumber,
                qq = qq,
                weChat = weChat,
                email = email,
                biography = biography,
                taught = taught,
                teaching = teaching,
                philosophy = philosophy,
                slogan = slogan,
            )
        }
    }
}

/**
 * 教授课程。
 *
 * @param name 课程名称
 * @param category 课程类别
 * @param semester 学期
 */
data class SimpleCourse(
    val name: String,
    val category: String,
    val semester: Semester,
)