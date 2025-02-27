package org.kiteio.punica.client.academic.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.academic.AcademicSystem
import org.kiteio.punica.client.academic.foundation.Term

/**
 * 返回课程考试和资质认证考试成绩。
 */
suspend fun AcademicSystem.getGrades(): Grades {
    return withContext(Dispatchers.Default) {
        val pair = getCourseGrades()
        return@withContext Grades(
            userId,
            pair.first.reversed(),
            pair.second,
            getQualificationGrades().reversed(),
        )
    }
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
data class Grades(
    val userId: String,
    val courses: List<CourseGrade>,
    val overview: String,
    val qualifications: List<QualificationGrade>,
)


/**
 * 成绩。
 *
 * @property name 名称
 * @property score 成绩
 */
sealed interface Grade {
    val name: String
    val score: String
}


/**
 * 返回资质认证考试成绩。
 */
private suspend fun AcademicSystem.getQualificationGrades(): List<QualificationGrade> {
    return withContext(Dispatchers.Default) {
        val text = get("jsxsd/kscj/djkscj_list").bodyAsText()

        val doc = Ksoup.parse(text)
        val tds = doc.getElementsByTag("td")

        val grades = mutableListOf<QualificationGrade>()
        // 范围排除 Logo 和尾部加载中
        for (index in 1..<tds.size - 1 step 9) {
            grades.add(
                QualificationGrade(
                    name = tds[index + 1].text(),
                    score = tds[index + 4].text(),
                    date = LocalDate.parse(tds[index + 8].text()),
                )
            )
        }

        return@withContext grades
    }
}


/**
 * 资质认证考试成绩。
 *
 * @property date 日期
 */
@Serializable
data class QualificationGrade(
    override val name: String,
    override val score: String,
    val date: LocalDate,
) : Grade


/**
 * 返回课程考试成绩和概览。
 */
private suspend fun AcademicSystem.getCourseGrades(): Pair<List<CourseGrade>, String> {
    return withContext(Dispatchers.Default) {
        val text = get("jsxsd/kscj/cjcx_list").bodyAsText()

        val doc = Ksoup.parse(text)
        val tds = doc.getElementsByTag("td")

        val grades = mutableListOf<CourseGrade>()
        // 范围排除 Logo
        for (index in 1..<tds.size step 17) {
            grades.add(
                CourseGrade(
                    name = tds[index + 3].text(),
                    score = tds[index + 7].text(),
                    term = Term.parse(tds[index + 1].text()),
                    courseId = tds[index + 2].text(),
                    dailyScore = tds[index + 4].text().ifEmpty { null },
                    labScore = tds[index + 5].text().ifEmpty { null },
                    finalScore = tds[index + 6].text().ifEmpty { null },
                    credits = tds[index + 8].text().toDouble(),
                    hours = tds[index + 9].text(),
                    assessmentMethod = tds[index + 10].text(),
                    category = tds[index + 11].text(),
                    type = tds[index + 12].text(),
                    electiveCategory = tds[index + 13].text().ifEmpty { null },
                    examType = tds[index + 14].text(),
                    mark = tds[index + 15].text().ifEmpty { null },
                    note = tds[index + 16].text().ifEmpty { null },
                )
            )
        }

        // <div class="Nsb_pw">
        val overview = doc.body().child(4).run {
            // 移除 <br>
            firstElementChild()?.remove()
            // 移除 <div>
            firstElementChild()?.remove()
            // 移除 <table>
            lastElementChild()?.remove()
            // 去除“查询条件：全部 ”
            text().replace("查询条件：全部 ", "")
        }

        return@withContext grades to overview
    }
}


/**
 * 课程成绩。
 *
 * @property term 学期
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
data class CourseGrade(
    override val name: String,
    override val score: String,
    val term: Term,
    val courseId: String,
    val dailyScore: String?,
    val labScore: String?,
    val finalScore: String?,
    val credits: Double,
    val hours: String,
    val assessmentMethod: String,
    val category: String,
    val type: String,
    val electiveCategory: String?,
    val examType: String,
    val mark: String?,
    val note: String?,
) : Grade