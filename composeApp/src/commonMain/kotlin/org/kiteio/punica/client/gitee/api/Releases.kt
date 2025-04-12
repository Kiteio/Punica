package org.kiteio.punica.client.gitee.api

import io.ktor.client.call.body
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.char
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.kiteio.punica.client.gitee.Gitee

/**
 * 返回 Gitee 仓库下的发行版。
 */
suspend fun Gitee.getReleases(owner: String, repo: String): List<Release> =
    get("api/v5/repos/$owner/$repo/releases")
        .body<List<ReleaseImpl>>().reversed()


/**
 * 发行版。
 *
 * @property name 版本名称
 * @property tag 标签
 * @property description 版本描述
 * @property time 发行时间
 * @property assets 资产
 */
sealed interface Release {
    val name: String
    val tag: String
    val description: String
    val time: LocalDateTime
    val assets: List<Assets>
}


@Serializable
private data class ReleaseImpl(
    override val name: String,
    @SerialName("tag_name") override val tag: String,
    val body: String,
    @SerialName("created_at") val createdAt: String,
    override val assets: List<Assets>,
) : Release {
    override val description = body.replace("\r\n", "\n")

    override val time = LocalDateTime.parse(
        createdAt,
        LocalDateTime.Format {
            year(); char('-')
            monthNumber(); char('-')
            dayOfMonth(); char('T')
            hour(); char(':')
            minute(); char(':')
            second(); chars("+08:00")
        },
    )
}


/**
 * 资产。
 *
 * @property name 文件名
 * @property urlString Url
 */
@Serializable
data class Assets(
    val name: String,
    @SerialName("browser_download_url") val urlString: String,
)