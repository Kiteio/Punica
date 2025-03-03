package org.kiteio.punica.client.course.api

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.isoDayNumber
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.kiteio.punica.client.academic.foundation.Campus
import org.kiteio.punica.client.course.CourseSystem
import org.kiteio.punica.client.course.foundation.CourseCategory
import org.kiteio.punica.client.course.foundation.CourseCategory.*
import org.kiteio.punica.client.course.foundation.Section
import org.kiteio.punica.serialization.Json

/**
 * 返回搜索到的课程列表。
 */
suspend fun CourseSystem.search(
    courseCategory: CourseCategory,
    parameters: SearchParameters = SearchParameters.Empty,
    pageIndex: Int = 0,
    pageSize: Int = 15,
): List<SCourse> {
    return withContext(Dispatchers.Default) {
        return@withContext submitForm(
            "jsxsd/xsxkkc/xsxk${courseCategory.search}xk",
            parameters {
                append("iDisplayStart", "${pageSize * pageIndex}")
                append("iDisplayLength", "$pageSize")
            },
        ) {
            when (courseCategory) {
                GENERAL, PROFESSIONAL, CROSS_GRADE, INTERPROFESSIONAL -> {
                    with(parameters) {
                        parameter("kcxx", name.encodeURLParameter())
                        parameter("skls", teacher.encodeURLParameter())
                        parameter("skxq", dayOfWeek?.isoDayNumber ?: "")
                        parameter("skjc", section?.let { "$it-" } ?: "")
                        parameter("sfym", filterFull)
                        parameter("sfct", filterConflicts)
                        if (courseCategory == GENERAL) {
                            parameter("xq", campus?.run { ordinal + 1 } ?: "")
                        }
                    }
                }

                else -> {}
            }
        }.run { Json.decodeFromString<SearchBody>(bodyAsText()) }.list
    }
}


/**
 * 搜索参数。
 *
 * @property name 课程名称
 * @property teacher 教师
 * @property dayOfWeek 星期
 * @property section 节次
 * @property campus 校区
 * @property filterFull 是否过滤无剩余量
 * @property filterConflicts 是否过滤冲突课程
 */
data class SearchParameters(
    val name: String = "",
    val teacher: String = "",
    val dayOfWeek: DayOfWeek? = null,
    val section: Section? = null,
    val campus: Campus? = null,
    val filterFull: Boolean = false,
    val filterConflicts: Boolean = false,
) {
    companion object {
        val Empty = SearchParameters()
    }
}


/**
 * 搜索响应内容。
 *
 * @property list 课程列表
 */
@Serializable
private data class SearchBody(
    @SerialName("aaData") val list: List<SCourseImpl>,
)


/**
 * 可选课程。
 *
 * @property id 操作 id
 * @property courseId 课程编号
 * @property name 课程名称
 * @property credits 学分
 * @property campusId 校区 id：1，2
 * @property time 上课时间
 * @property classroom 教室
 * @property total 课堂人数
 * @property leftover 剩余量
 * @property note 备注
 * @property conflict 选课冲突情况
 * @property type 类别
 * @property isSelectable 是否开放选课
 * @property isSelected 是否已选
 * @property courseProvider 开课单位
 * @property arrangements 上课安排
 */
interface SCourse {
    val id: String
    val courseId: String
    val name: String
    val credits: Double
    val teacher: String
    val campusId: Int
    val time: String?
    val classroom: String?
    val total: Int
    val leftover: Int
    val note: String?
    val conflict: String?
    val type: String?
    val isSelectable: Boolean
    val isSelected: Boolean
    val courseProvider: String
    val assessmentMethod: String
    val arrangements: List<CourseArrangement>?
}


@Serializable
data class SCourseImpl(
    @SerialName("jx0404id") override val id: String,
    @SerialName("kch") override val courseId: String,
    @SerialName("kcmc") override val name: String,
    @SerialName("xf") override val credits: Double,
    @SerialName("skls") override val teacher: String,
    @SerialName("xqid") override val campusId: Int,
    val sksj: String,
    val skdd: String,
    @SerialName("xxrs") override val total: Int,
    @SerialName("syrs") override val leftover: Int,
    val bj: String?,
    val ctsm: String? = null,
    @SerialName("szkcflmc") override val type: String?,
    @SerialName("sfkfxk") @Serializable(BooleanSerializer::class)
    override val isSelectable: Boolean,
    @SerialName("sfYx") @Serializable(BooleanSerializer::class)
    override val isSelected: Boolean,
    @SerialName("dwmc") override val courseProvider: String,
    @SerialName("ksfs") override val assessmentMethod: String,
    @SerialName("kkapList") override val arrangements: List<CourseArrangement>? = null,
) : SCourse {
    override val time = sksj.takeIf { it != "&nbsp;" }
    override val classroom = skdd.takeIf { it != "&nbsp;" }
    override val note = bj.takeIf { it != "拟开课时间:" }
    override val conflict = ctsm.takeIf { it?.isNotEmpty() == true }
}


/**
 * 上课安排。
 *
 * @property classroom 教室
 * @property weeks 周次，如“1-16”
 * @property isoDayNumber 星期
 * @property sections 节次，如“1-2”
 */
@Serializable
data class CourseArrangement(
    @SerialName("jsmc") val classroom: String,
    @SerialName("kkzc") val weeks: String,
    @SerialName("xq") val isoDayNumber: Int,
    @SerialName("skjcmc") val sections: String,
)


/**
 * [Boolean] 序列器。
 */
class BooleanSerializer : KSerializer<Boolean> {
    override val descriptor = PrimitiveSerialDescriptor(this::class.qualifiedName!!, PrimitiveKind.STRING)


    /**
     * 将 [Int] [Boolean] 反序列化为 [Boolean]。
     */
    override fun deserialize(decoder: Decoder) = decoder.decodeString().let { it != "0" || it == "false" }


    /**
     * 将 [value] 序列化。
     */
    override fun serialize(encoder: Encoder, value: Boolean) = encoder.encodeBoolean(value)
}