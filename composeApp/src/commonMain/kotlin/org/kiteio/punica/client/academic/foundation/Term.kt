package org.kiteio.punica.client.academic.foundation

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.kiteio.punica.wrapper.now

/**
 * 学期。
 *
 * @property startYear 开始年份
 * @property ordinal 学期编号
 */
@Serializable
data class Term(val startYear: Int, val ordinal: Int) {
    init {
        require(ordinal in 1..2)
    }

    /**
     * 返回“yyyy-yyyy-n”的字符串。
     */
    override fun toString() = "$startYear-${startYear + 1}-$ordinal"


    companion object {
        /** 当前学期，1-20 ..< 7-20为一个学期 */
        val current by lazy {
            LocalDate.now().run {
                val isBefore720 = monthNumber == 7 && dayOfMonth < 20
                val startYear = if (monthNumber < 7 || isBefore720) year - 1 else year

                val isAfter120 = monthNumber == 1 && dayOfMonth >= 20
                val ordinal = if (isAfter120 || monthNumber in 2..6 || isBefore720) 2 else 1

                Term(startYear, ordinal)
            }
        }


        /**
         * 将“yyyy-yyyy-n”的 [string] 转化为 [Term]。
         */
        fun parse(string: String) = string.split("-").run {
            Term(get(0).toInt(), get(2).toInt())
        }
    }
}