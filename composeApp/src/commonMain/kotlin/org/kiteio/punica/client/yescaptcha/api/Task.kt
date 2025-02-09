package org.kiteio.punica.client.yescaptcha.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.yescaptcha.YesCaptcha

/**
 * 返回 [base64] 图片中的文字。
 *
 * [参阅](https://yescaptcha.atlassian.net/wiki/spaces/YESCAPTCHA/pages/33351/createTask)。
 */
suspend fun YesCaptcha.createTask(base64: String): String {
    return post("createTask") {
        setBody(TaskRequestBody(key, Task("ImageToTextTaskMuggle", base64)))
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }.body<TaskResultBody>().solution["text"]!!
}


/**
 * 识别任务请求体。
 *
 * @property clientKey 客户端密钥
 * @property task 识别任务。
 */
@Serializable
private data class TaskRequestBody(val clientKey: String, val task: Task)


/**
 * 识别任务。
 *
 * @property type 识别类型
 * @property body 待识别内容。
 */
@Serializable
private data class Task(val type: String, val body: String)


/**
 * 识别结果。
 *
 * @property solution 识别结果。
 */
@Serializable
private data class TaskResultBody(val solution: Map<String, String>)