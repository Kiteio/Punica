package org.kiteio.punica.client.course.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
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
import org.kiteio.punica.serialization.Identifiable

/**
 * 返回搜索到的课程列表。
 */
suspend fun CourseSystem.search(
    courseCategory: CourseCategory,
    parameters: SearchParameters = SearchParameters.Empty,
    pageIndex: Int = 0,
    pageSize: Int = 15,
) = submitForm(
    "jsxsd/xsxkkc/xsxk${courseCategory.search}xk",
    parameters {
        append("iDisplayStart", "${pageSize * pageIndex}")
        append("iDisplayLength", "$pageSize")
    },
) {
    when(courseCategory) {
        GENERAL, PROFESSIONAL, CROSS_GRADE, INTERPROFESSIONAL -> {
            with(parameters) {
                parameter("kcxx", name?.encodeURLParameter() ?: "")
                parameter("skls", teacher?.encodeURLParameter() ?: "")
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
}.body<SearchBody>().list


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
    val name: String? = null,
    val teacher: String? = null,
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
    @SerialName("aaData") val list: List<SCourse>,
)


/**
 * 可选课程。
 *
 * @property id 操作 id
 * @property courseId 课程编号
 * @property name 课程名称
 * @property credits 学分
 * @property campusId 校区 id：0，1
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
@Serializable
data class SCourse(
    @SerialName("jx0404id") override val id: String,
    @SerialName("kch") val courseId: String,
    @SerialName("kcmc") val name: String,
    @SerialName("xf") val credits: Double,
    @SerialName("skls") val teacher: String,
    @SerialName("xqid") val campusId: Int,
    @SerialName("sksj") val time: String,
    @SerialName("skdd") val classroom: String,
    @SerialName("xxrs") val total: Int,
    @SerialName("syrs") val leftover: Int,
    @SerialName("bj") val note: String?,
    @SerialName("ctsm") val conflict: String,
    @SerialName("szkcflmc") val type: String,
    @SerialName("sfkfxk") @Serializable(BooleanSerializer::class)
    val isSelectable: Boolean,
    @SerialName("sfYx") @Serializable(BooleanSerializer::class)
    val isSelected: Boolean,
    @SerialName("dwmc") val courseProvider: String,
    @SerialName("ksfs") val assessmentMethod: String,
    @SerialName("kkapList") val arrangements: List<CourseArrangement>,
) : Identifiable<String>


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