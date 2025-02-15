package org.kiteio.punica.client.course.foundation

/**
 * 节次。
 */
enum class Section(private val duration: ClosedRange<Int>) {
    FIRST(1..2), SECOND(3..4),
    THIRD(5..6), FOURTH(7..8),
    FIFTH(9..10), SIXTH(11..12);


    override fun toString() = "${duration.start}-${duration.endInclusive}"
}