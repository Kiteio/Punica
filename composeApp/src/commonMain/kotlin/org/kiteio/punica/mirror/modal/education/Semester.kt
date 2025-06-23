package org.kiteio.punica.mirror.modal.education

import kotlinx.datetime.LocalDate
import org.kiteio.punica.wrapper.now

/**
 * 学期。
 *
 * @property year 开始年份
 * @property term 学期分段
 */
data class Semester(val year: Int, val term: Term) {
    /**
     * yyyy-yyyy-T。
     */
    override fun toString() = "$year-${year + 1}-${term.value}"

    companion object {
        /** 当前学期 */
        val now by lazy {
            val now = LocalDate.now()
            val year = now.year

            when {
                // 去年第 1 学期
                now < LocalDate(year, 1, 20) -> Semester(
                    year - 1,
                    Term.First
                )

                // 去年第 2 学期
                now < LocalDate(year, 7, 20) -> Semester(
                    year - 1,
                    Term.Second
                )

                // 今年第 1 学期
                else -> Semester(
                    year,
                    Term.First
                )
            }
        }

        /**
         * 将 [semesterString] 解析为 [Semester]。
         *
         * @param semesterString 学期字符串，“yyyy-yyyy-T”
         */
        fun parse(semesterString: String) = semesterString.split("-").run {
            Semester(
                get(0).toInt(),
                if (get(2).toInt() == Term.First.value)
                    Term.First else Term.Second,
            )
        }
    }

    /**
     * 学期分段。
     */
    sealed class Term(val value: Int) {
        /** 上学期 */
        data object First : Term(1)

        /** 下学期 */
        data object Second : Term(2)
    }
}