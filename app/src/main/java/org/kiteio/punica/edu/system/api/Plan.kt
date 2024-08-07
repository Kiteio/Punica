package org.kiteio.punica.edu.system.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.kiteio.punica.datastore.Identified
import org.kiteio.punica.edu.foundation.Semester
import org.kiteio.punica.edu.system.EduSystem

/**
 * 执行计划
 * @receiver [EduSystem]
 * @return [Plan]
 */
suspend fun EduSystem.plan() = withContext(Dispatchers.Default) {
    val text = session.fetch(route { PLAN }).bodyAsText()

    val document = Ksoup.parse(text)
    val table = document.getElementById("dataList")!!
    val rows = table.getElementsByTag("tr")

    val items = arrayListOf<PlanItem>()
    for (index in 1..<rows.size) {
        val infos = rows[index].children()

        items.add(
            PlanItem(
                id = infos[2].text(),
                semester = Semester.of(infos[1].text()),
                name = infos[3].text(),
                department = infos[4].text(),
                point = infos[5].text(),
                classHours = infos[6].text(),
                examMode = infos[7].text(),
                type = infos[8].text()
            )
        )
    }

    return@withContext Plan(name, items)
}


/**
 * 执行计划
 * @property username 学号
 * @property items
 */
@Serializable
class Plan(
    val username: String,
    val items: List<PlanItem>
): Identified() {
    override val id = username
}


/**
 * 执行计划项
 * @property id 课程编号
 * @property semester 学期
 * @property name 课程名
 * @property department 开课部门
 * @property point 学分
 * @property classHours 总学时
 * @property examMode 考核方式
 * @property type 课程属性
 */
@Serializable
data class PlanItem(
    val id: String,
    val semester: Semester,
    val name: String,
    val department: String,
    val point: String,
    val classHours: String,
    val examMode: String,
    val type: String
)