package org.kiteio.punica.client.academic.api

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.select.Evaluator
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.kiteio.punica.client.academic.AcademicSystem
import org.kiteio.punica.client.academic.foundation.Term

/**
 * 返回工号为 [teacherId] 的教师信息。
 */
suspend fun AcademicSystem.getTeacherProfile(teacherId: String): TeacherProfile {
    val text = get("jsxsd/jsxx/jsxx_query_detail") {
        parameter("jg0101id", teacherId)
    }.bodyAsText()

    val doc = Ksoup.parse(text)
    val trs = doc.selectFirst(Evaluator.Class("no_border_table"))!!
        .child(0).children()

    return TeacherProfileBuilder().apply {
        var hasContact = false

        // 范围排除“基本信息”
        for (index in 1..<trs.size) {
            when (index) {
                1 -> {
                    name = trs[index].child(2).text()
                    gender = trs[index].child(4).text().takeIf { it != "未说明的性别" }
                }

                2 -> {
                    politics = trs[index].child(1).text().ifEmpty { null }
                    nation = trs[index].child(3).text().ifEmpty { null }
                }

                3 -> {
                    duty = trs[index].child(1).text().ifEmpty { null }
                    title = trs[index].child(3).text().ifEmpty { null }
                }

                4 -> {
                    category = trs[index].child(1).text().ifEmpty { null }
                    faculty = trs[index].child(3).text()
                }

                5 -> {
                    office = trs[index].child(1).text().takeIf { it != "无" }
                    qualification = trs[index].child(3).text().ifEmpty { null }
                }

                6 -> {
                    degree = trs[index].child(2).text().ifEmpty { null }
                    field = trs[index].child(4).text().ifEmpty { null }
                }

                7 -> {
                    if (trs[index].child(1).text() != "联系方式：") {
                        hasContact = true
                        phoneNumber = trs[index].child(2).text().ifEmpty { null }
                        qq = trs[index].child(4).text().ifEmpty { null }
                    }
                }

                8 -> {
                    if (hasContact) {
                        weChat = trs[index].child(2).text().ifEmpty { null }
                        email = trs[index].child(4).text().ifEmpty { null }
                    }
                }

                trs.lastIndex - 8 -> {
                    // 个人简介
                    biography = trs[index].text().takeIf { it != "暂无数据" }
                }

                trs.lastIndex - 6, trs.lastIndex - 4 -> {
                    // 近四个学期主讲课程、下学期计划开设课程
                    val tds = trs[index].selectFirst(Evaluator.Tag("tbody"))!!.getElementsByTag("td")
                    if (tds.size != 1) {
                        val courses = if (index == trs.lastIndex - 6) teaching else taught
                        for (tdIndex in tds.indices step 4) {
                            courses.add(
                                TaughtCourse(
                                    name = tds[tdIndex + 1].text(),
                                    sort = tds[tdIndex + 2].text(),
                                    term = Term.parse(tds[tdIndex + 3].text()),
                                )
                            )
                        }
                    }
                }

                trs.lastIndex - 2 -> {
                    // 教育理念
                    philosophy = trs[index].text().takeIf { it != "暂无数据" }
                }

                trs.lastIndex -> {
                    // 最想对学生说的话
                    slogan = trs[index].text().takeIf { it != "暂无数据" }
                }
            }
        }
    }.build(teacherId)
}


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
 * @property faculty 部门（院系）
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
data class TeacherProfile(
    val id: String,
    val name: String,
    val gender: String?,
    val politics: String?,
    val nation: String?,
    val duty: String?,
    val title: String?,
    val category: String?,
    val faculty: String?,
    val office: String?,
    val qualification: String?,
    val degree: String?,
    val field: String?,
    val phoneNumber: String?,
    val qq: String?,
    val weChat: String?,
    val email: String?,
    val biography: String?,
    val taught: List<TaughtCourse>,
    val teaching: List<TaughtCourse>,
    val philosophy: String?,
    val slogan: String?,
)


/**
 * 主讲课程。
 *
 * @property name 课程名称
 * @property sort 课程类别
 * @property term 学期
 */
data class TaughtCourse(val name: String, val sort: String, val term: Term)


/**
 * [TeacherProfile] 建造者。
 */
private class TeacherProfileBuilder {
    lateinit var name: String
    var gender: String? = null
    var politics: String? = null
    var nation: String? = null
    var duty: String? = null
    var title: String? = null
    var category: String? = null
    lateinit var faculty: String
    var office: String? = null
    var qualification: String? = null
    var degree: String? = null
    var field: String? = null
    var phoneNumber: String? = null
    var qq: String? = null
    var weChat: String? = null
    var email: String? = null
    var biography: String? = null
    val taught = mutableListOf<TaughtCourse>()
    val teaching = mutableListOf<TaughtCourse>()
    var philosophy: String? = null
    var slogan: String? = null


    fun build(id: String) = TeacherProfile(
        id,
        name,
        gender,
        politics,
        nation,
        duty,
        title,
        category,
        faculty,
        office,
        qualification,
        degree,
        field,
        phoneNumber,
        qq,
        weChat,
        email,
        biography,
        taught,
        teaching,
        philosophy,
        slogan,
    )
}