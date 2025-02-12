package org.kiteio.punica.client.academic.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.*
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.academic.AcademicSystem

/**
 * 返回课程考试和资质认证考试成绩。
 */
suspend fun AcademicSystem.getGrades(): Grades {
    val pair = getCoursesGrade()
    return Grades(userId, pair.first, pair.second, getQualificationsGrade())
}


/**
 * 成绩。
 *
 * @property userId 学号
 * @property courses 课程考试
 * @property overview 课程考试概览
 * @property qualifications 资格认证考试
 */
@Serializable
class Grades(
    val userId: Long,
    val courses: List<CourseGrade>,
    val overview: String,
    val qualifications: List<Grade>,
)


/**
 * 返回资质认证考试成绩。
 */
private suspend fun AcademicSystem.getQualificationsGrade(): List<Grade> {
    val text = get("jsxsd/kscj/djkscj_list").bodyAsText()

    val document = Ksoup.parse(text)
    val tds = document.getElementsByTag("td")

    val grades = mutableListOf<Grade>()
    // 范围排除 Logo 和尾部加载中
    for (index in 1..<tds.size - 1 step 9) {
        grades.add(object : Grade() {
            override val name = tds[index + 1].text()
            override val score = tds[index + 4].text()
            override val time = tds[index + 8].text()
        })
    }

    return grades
}


/**
 * 成绩。
 *
 * @property name 名称
 * @property score 分数
 * @property time 时间
 */
@Serializable
abstract class Grade {
    abstract val name: String
    abstract val score: String
    abstract val time: String
}


/**
 * 返回课程考试成绩和概览。
 */
private suspend fun AcademicSystem.getCoursesGrade(): Pair<List<CourseGrade>, String> {
    val text = get("jsxsd/kscj/cjcx_list").bodyAsText()

    val document = Ksoup.parse(text)
    val tds = document.getElementsByTag("td")

    val grades = mutableListOf<CourseGrade>()
    // 范围排除 Logo
    for (index in 1..<tds.size step 17) {
        grades.add(
            CourseGrade(
                name = tds[index + 3].text(),
                score = tds[index + 7].text(),
                time = tds[index + 1].text(),
                courseId = tds[index + 2].text(),
                dailyScore = tds[index + 4].text(),
                labScore = tds[index + 5].text(),
                finalScore = tds[index + 6].text(),
                credits = tds[index + 8].text(),
                hours = tds[index + 9].text(),
                assessmentMethod = tds[index + 10].text(),
                category = tds[index + 11].text(),
                type = tds[index + 12].text(),
                electiveCategory = tds[index + 13].text(),
                examType = tds[index + 14].text(),
                mark = tds[index + 15].text(),
                note = tds[index + 16].text(),
            )
        )
    }

    // <div class="Nsb_pw">
    val overview = document.body().child(4).run {
        // 移除 <br>
        firstElementChild()?.remove()
        // 移除 <div>
        firstElementChild()?.remove()
        // 移除 <table>
        lastElementChild()?.remove()
        // 去除“查询条件：全部 ”
        text().replace("查询条件：全部 ", "")
    }

    return grades to overview
}


/**
 * 课程成绩。
 *
 * @property name 课程名称
 * @property score 成绩
 * @property time 学期
 * @property courseId 课程编号
 * @property dailyScore 平时成绩
 * @property labScore 实验成绩
 * @property finalScore 期末成绩
 * @property credits 学分
 * @property hours 总学时
 * @property assessmentMethod 考核方式
 * @property category 课程属性
 * @property type 课程性质
 * @property electiveCategory 通识课分类
 * @property examType 考试性质
 * @property mark 成绩标识
 * @property note 备注
 */
@Serializable
class CourseGrade(
    override val name: String,
    override val score: String,
    override val time: String,
    val courseId: String,
    val dailyScore: String,
    val labScore: String,
    val finalScore: String,
    val credits: String,
    val hours: String,
    val assessmentMethod: String,
    val category: String,
    val type: String,
    val electiveCategory: String,
    val examType: String,
    val mark: String,
    val note: String,
) : Grade()