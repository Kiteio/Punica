package org.kiteio.punica.client.academic.api

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.select.Evaluator
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.client.academic.AcademicSystem

/**
 * 返回教师。
 */
suspend fun AcademicSystem.getTeachers(name: String, pageIndex: Int = 1): Teachers {
    return withContext(Dispatchers.Default) {
        val text = submitForm(
            "jsxsd/jsxx/jsxx_list",
            parameters {
                append("jsxm", name)
                // 院系查询，院系过多，暂不考虑
                // append("kkyx", "10000")
                append("pageIndex", "$pageIndex")
            },
        ).bodyAsText()

        val doc = Ksoup.parse(text)
        val tds = doc.getElementsByTag("td")

        // 搜索结果为空
        if (tds.size == 2) {
            return@withContext Teachers(0, emptyList())
        }

        val teachers = mutableListOf<Teacher>()
        // 范围排除 Logo
        for (index in 1..<tds.size step 5) {
            teachers.add(
                Teacher(
                    id = tds[index + 1].text(),
                    name = tds[index + 2].text(),
                    faculty = tds[index + 3].text().ifEmpty { null },
                )
            )
        }

        // 页码（共%d页 %d条）
        val div = doc.selectFirst(Evaluator.Class("Nsb_r_list_fy3"))

        return@withContext Teachers(
            Regex("\\d+").find(div!!.text())!!.value.toInt(),
            teachers,
        )
    }
}


/**
 * 教师。
 *
 * @property pageCount 页数
 * @property teachers 教师
 */
data class Teachers(
    val pageCount: Int,
    val teachers: List<Teacher>,
)


/**
 * 教师。
 *
 * @property id 工号
 * @property name 姓名
 * @property faculty 院系
 */
data class Teacher(
    val id: String,
    val name: String,
    val faculty: String?,
)