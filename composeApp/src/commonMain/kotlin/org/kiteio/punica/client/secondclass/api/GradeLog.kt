package org.kiteio.punica.client.secondclass.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.secondclass.SecondClass
import org.kiteio.punica.client.secondclass.foundation.SecondClassBody
import org.kiteio.punica.wrapper.timestampToString

/**
 * 返回成绩获取记录。
 */
suspend fun SecondClass.getGradeLogs(): List<GradeLog> {
    val body = get("apps/user/achievement/by-classify-list-detail") {
        header("X-Token", token)
    }.body<GradeLogsBody>()

    require(body.code == 200) { body.msg }

    return body.data
}


/**
 * 成绩获取记录。
 *
 * @property activityName 活动名称
 * @property category 分类
 * @property score 分数
 * @property time 时间
 * @property term 学期
 */
interface GradeLog {
    val activityName: String
    val category: String
    val score: Double
    val time: String
    val term: String
}


/**
 * 成绩获取记录响应内容。
 */
@Serializable
private data class GradeLogsBody(
    override val code: Int,
    override val msg: String,
    override val data: List<GradeLogImpl>,
) : SecondClassBody<List<GradeLogImpl>>


/**
 * 成绩获取记录。
 */
@Serializable
private data class GradeLogImpl(
    val actName: String?,
    val proName: String?,
    val optName: String?,
    @SerialName("className") override val category: String,
    @SerialName("score") override val score: Double,
    @SerialName("sendTime") val timestamp: Long,
    @SerialName("xueqiName") override val term: String,
) : GradeLog {
    override val activityName = actName ?: proName ?: optName ?: ""
    override val time = timestampToString(timestamp)
}