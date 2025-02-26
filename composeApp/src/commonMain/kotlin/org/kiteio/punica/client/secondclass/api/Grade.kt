package org.kiteio.punica.client.secondclass.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.kiteio.punica.client.secondclass.SecondClass
import org.kiteio.punica.client.secondclass.foundation.SecondClassBody

/**
 * 返回成绩。
 */
suspend fun SecondClass.getGrades(): SecondClassGrades {
    val body = get("apps/user/achievement/by-classify-list") {
        parameter("para", Json.encodeToString(mapOf("userId" to id)))
        header("X-Token", token)
    }.body<GradesBody>()

    require(body.code == 200) { body.msg }

    return SecondClassGrades(userId, body.data as List<SecondClassGrade>)
}


/**
 * 第二课题成绩。
 *
 * @property userId 学号
 * @property grades 成绩
 */
@Serializable
data class SecondClassGrades(
    val userId: String,
    val grades: List<SecondClassGrade>,
)


/**
 * 第二课堂成绩。
 *
 * @property name 名称
 * @property score 分数
 * @property requiredScore 要求分数
 */
@Serializable
data class SecondClassGrade(
    @SerialName("classifyName") val name: String,
    @SerialName("classifyHours") val score: Double,
    @SerialName("classifySchoolMinHours") val requiredScore: Double,
)


/**
 * 成绩响应内容。
 */
@Serializable
private data class GradesBody(
    override val code: Int,
    override val msg: String,
    override val data: List<SecondClassGrade>,
) : SecondClassBody<List<SecondClassGrade>>