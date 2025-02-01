package org.kiteio.punica.client.yescaptcha.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.yescaptcha.YesCaptchaClient

/**
 * 返回 [base64] 图片中的文字。
 *
 * [参阅](https://yescaptcha.atlassian.net/wiki/spaces/YESCAPTCHA/pages/33351/createTask)。
 */
suspend fun YesCaptchaClient.createTask(base64: String): String {
    return post("createTask") {
        setBody(TaskRequestBody(key, Task("ImageToTextTaskMuggle", base64)))
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }.body<TaskResultBody>().solution["text"]!!
}


/**
 * 识别任务请求体。
 *
 * [clientKey] 为客户端密钥，[task] 包含了任务描述。
 */
@Serializable
private data class TaskRequestBody(val clientKey: String, val task: Task)


/**
 * 识别任务描述。
 *
 * [type] 为识别类型，[body] 中包含了待识别内容。
 */
@Serializable
private data class Task(val type: String, val body: String)


/**
 * 识别结果。
 *
 * [solution] 中包含了识别结果。
 */
@Serializable
private data class TaskResultBody(val solution: Map<String, String>)