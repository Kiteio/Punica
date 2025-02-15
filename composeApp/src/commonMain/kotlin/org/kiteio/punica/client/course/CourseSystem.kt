package org.kiteio.punica.client.course

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.jetbrains.compose.resources.getString
import org.kiteio.punica.client.academic.AcademicSystem
import org.kiteio.punica.http.HttpClientWrapper
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.course_system_inaccessible

/**
 * 选课系统。
 */
interface CourseSystem : HttpClientWrapper {
    /** 轮次 id */
    val id: String

    /** 学号 */
    val userId: Long
}


/**
 * 返回选课系统。
 */
suspend fun AcademicSystem.CourseSystem(courseSystemId: String? = null): CourseSystem {
    courseSystemId?.let {
        // 通过 id 直接进入选课系统
        val text = get("jsxsd/xsxk/xsxk_index") {
            parameter("jx0502zbid", it)
        }.bodyAsText()

        when (Ksoup.parse(text).body().ownText().trim()) {
            "当前未开放选课，具体请查看学校选课通知！",
            "选课操作失败！错误内容:ORA-01403: no data found",
                -> {}
            else -> return object : CourseSystem {
                override val httpClient = this@CourseSystem.httpClient
                override val id = it
                override val userId = this@CourseSystem.userId
            }
        }
    }

    // 从选课中心进入选课系统
    val text = get("jsxsd/xsxk/xklc_list?a=1&b=2").bodyAsText()

    val doc = Ksoup.parse(text)
    val tds = doc.getElementsByTag("td")

    // 只有 Logo
    if (tds.size != 1) {
        val urlString = tds[7].child(0).attr("href")
        val id = parseQueryString(get(urlString).request.url.encodedQuery)["jx0502zbid"]!!

        object : CourseSystem {
            override val httpClient = this@CourseSystem.httpClient
            override val id = id
            override val userId = this@CourseSystem.userId
        }
    }
    error(getString(Res.string.course_system_inaccessible))
}