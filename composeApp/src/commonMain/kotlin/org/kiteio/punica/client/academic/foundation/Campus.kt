package org.kiteio.punica.client.academic.foundation

import org.jetbrains.compose.resources.StringResource
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.campus_canton
import punica.composeapp.generated.resources.campus_foshan

/**
 * 校区。
 *
 * @property nameRes 名称字符串资源
 */
enum class Campus(val nameRes: StringResource) {
    /** 广州校区 */
    CANTON(Res.string.campus_canton),

    /** 佛山校区 */
    FO_SHAN(Res.string.campus_foshan)
}


/**
 * 校区唯一标识。
 */
val Campus.id get() = ordinal + 1