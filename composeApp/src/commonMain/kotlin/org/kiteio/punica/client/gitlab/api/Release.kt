package org.kiteio.punica.client.gitlab.api

import io.ktor.client.call.body
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.char
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.gitlab.GitLab

/**
 * 返回 GitLab 仓库下的发行版。
 */
suspend fun GitLab.getReleases(id: Int = 115274): List<Release> =
    get("/api/v4/projects/$id/releases").body<List<ReleaseImpl>>()


/**
 * 发行版。
 *
 * @property name 版本名称
 * @property tag 标签
 * @property description 版本描述
 * @property time 发行时间
 */
sealed interface Release {
    val name: String
    val tag: String
    val description: String
    val time: LocalDateTime
}


@Serializable
private data class ReleaseImpl(
    override val name: String,
    @SerialName("tag_name") override val tag: String,
    override val description: String,
    @SerialName("released_at") val realeasedAt: String,
) : Release {
    override val time = LocalDateTime.parse(
        realeasedAt,
        LocalDateTime.Format {
            year(); char('-')
            monthNumber(); char('-')
            dayOfMonth(); char('T')
            hour(); char(':')
            minute(); char(':')
            second(); chars(".000Z")
        }
    )
}