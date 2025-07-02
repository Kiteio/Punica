package org.kiteio.punica.mirror.modal.education

/**
 * 选课学分总览。
 *
 * @property progress 学分进度
 * @property note 选课轮次信息备注
 */
data class CoursesOverview(
    val progress: List<Progress>,
    val note: String,
) {
    /**
     * 学分进度。
     *
     * @property name 类别名称
     * @property credits 已有学分
     * @property limitCredits 学分限制
     */
    data class Progress(
        val name: String,
        val credits: String,
        val limitCredits: String,
    )
}