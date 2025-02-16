package org.kiteio.punica.client.secondclass.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.secondclass.SecondClass
import org.kiteio.punica.client.secondclass.foundation.ActivityType
import org.kiteio.punica.client.secondclass.foundation.SecondClassBody
import org.kiteio.punica.serialization.Json

/**
 * 返回活动。
 */
suspend fun SecondClass.getActivities() =
    getActivities("apps/activityImpl/list/getActivityByUser")


/**
 * 返回用户相关活动。
 */
suspend fun SecondClass.getMyActivities(type: ActivityType) =
    getActivities("apps/activityImpl/getmyjoinactivitylist", type)


/**
 * 返回活动。
 */
private suspend fun SecondClass.getActivities(
    urlString: String,
    type: ActivityType? = null,
): List<Activity> {
    val mutableMap = mutableMapOf("cur" to 1, "size" to 20)
    type?.let { mutableMap["type"] = it.ordinal }

    val body = get(urlString) {
        parameter("para", Json.encodeToString(mutableMap))
        header("X-Token", token)
    }.body<ActivitiesBody>()

    require(body.code == 200) { body.msg }

    return body.data.records
}


/**
 * 活动。
 *
 * @property id 唯一标识
 * @property name 活动名称
 * @property category 分类
 * @property score 分数
 * @property duration 持续时间
 * @property organization 组织
 * @property logoUrl Logo Url
 * @property type 类型
 * @property isOnline 是否为线上
 */
interface Activity {
    val id: Int
    val name: String
    val category: String
    val score: Double
    val duration: ClosedRange<String>
    val organization: String
    val logoUrl: String
    val type: String
    val isOnline: Boolean
}


/**
 * 活动响应内容。
 */
@Serializable
private data class ActivitiesBody(
    override val code: Int,
    override val msg: String,
    override val data: Records,
) : SecondClassBody<Records>


@Serializable
private data class Records(val records: List<ActivityImpl>)


/**
 * 活动
 */
@Serializable
data class ActivityImpl(
    override val id: Int,
    override val name: String,
    override val category: String,
    @SerialName("hours") override val score: Double,
    val startTime: String,
    val endTime: String,
    @SerialName("orgName") override val organization: String,
    @SerialName("logo") override val logoUrl: String,
    @SerialName("typeName") override val type: String,
    val oto: Int,
) : Activity {
    override val duration = startTime..endTime
    override val isOnline = oto == 1
}