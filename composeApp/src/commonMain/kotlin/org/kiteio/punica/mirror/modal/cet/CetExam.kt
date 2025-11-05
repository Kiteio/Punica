package org.kiteio.punica.mirror.modal.cet

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.AmPmMarker

/**
 * Cet 考试。
 *
 * @property name 考试名称
 * @property cet4Written 英语四级笔试时间
 * @property cet6Written 英语六级笔试时间
 * @property cet4Speaking 英语四级口语时间
 * @property cet6Speaking 英语六级口语时间
 * @property note 备注
 * @property pdfUrlString PDF 文件地址
 */
@Entity(tableName = "cet_exam")
data class CetExam(
    val name: String,
    @Embedded(prefix = "cet4_written_") val cet4Written: CetExamTime,
    @Embedded(prefix = "cet6_written_") val cet6Written: CetExamTime,
    @Embedded(prefix = "cet4_speaking_") val cet4Speaking: CetExamTime,
    @Embedded(prefix = "cet6_speaking_") val cet6Speaking: CetExamTime,
    val note: String,
    val pdfUrlString: String,
    @PrimaryKey val id: Int = 0
)

/**
 * Cet 考试时间。
 *
 * @property date 日期
 * @property amPmMarker 早上/下午标记
 */
data class CetExamTime(
    val date: LocalDate,
    val amPmMarker: AmPmMarker? = null,
)