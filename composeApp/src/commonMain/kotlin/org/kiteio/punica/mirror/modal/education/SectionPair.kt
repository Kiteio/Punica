package org.kiteio.punica.mirror.modal.education

/**
 * 节次对。
 */
sealed class SectionPair(private val duration: ClosedRange<Int>) {
    /** 1-2 */
    data object First : SectionPair(1..2)

    /** 3-4 */
    data object Second : SectionPair(3..4)

    /** 5-6 */
    data object Third : SectionPair(5..6)

    /** 7-8 */
    data object Fourth : SectionPair(7..8)

    /** 9-10 */
    data object Fifth : SectionPair(9..10)

    /** 11-12 */
    data object Sixth : SectionPair(11..12)

    override fun toString() = "${duration.start}-${duration.endInclusive}"
}