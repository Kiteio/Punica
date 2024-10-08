package org.kiteio.punica.edu.system.api.course

import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.candy.json
import org.kiteio.punica.candy.errorOnToastLayer
import org.kiteio.punica.edu.system.CourseSystem

/**
 * 退课
 * @receiver [CourseSystem]
 * @param operateId
 */
suspend fun CourseSystem.delete(operateId: String) = withContext(Dispatchers.Default) {
    session.fetch(route { DELETE }) {
        parameter("jx0404id", operateId)
    }.bodyAsText().json.run {
        // 退课成功只会有 success 值为 true
        // 退课失败会有额外 message 参数
        if(!getBoolean("success")) errorOnToastLayer(getString("message"))
    }
}