package org.kiteio.punica.client.academic.foundation

import kotlinx.serialization.Serializable

/**
 * 学期。
 *
 * @property startYear 开始年份
 * @property ordinal 学期编号
 */
@Serializable
data class Term(val startYear: Int, val ordinal: Int) {
    /**
     * 返回“yyyy-yyyy-n”的字符串。
     */
    override fun toString() = "$startYear-${startYear + 1}-$ordinal"


    companion object {
        /**
         * 将“yyyy-yyyy-n”的 [string] 转化为 [Term]。
         */
        fun parse(string: String) = string.split("-").run {
            Term(get(0).toInt(), get(2).toInt())
        }
    }
}