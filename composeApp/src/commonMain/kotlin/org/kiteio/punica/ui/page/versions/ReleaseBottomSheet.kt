package org.kiteio.punica.ui.page.versions

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.m3.Markdown
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.client.gitee.api.Release
import org.kiteio.punica.ui.component.CardListItem
import org.kiteio.punica.ui.component.ModalBottomSheet
import org.kiteio.punica.ui.component.PunicaListItem
import org.kiteio.punica.wrapper.format
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.github

@Composable
fun ReleaseBottomSheet(visible: Boolean, onDismissRequest: () -> Unit, release: Release?) {
    ModalBottomSheet(visible, onDismissRequest) {
        release?.run {
            val uriHandler = LocalUriHandler.current

            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                // 版本名称
                item {
                    PunicaListItem(
                        headlineContent = { Text(name) },
                        supportingContent = {
                            // 创建时间
                            Text(time.format(), style = MaterialTheme.typography.bodySmall)
                        },
                        trailingContent = {
                            TextButton(
                                onClick = {
                                    uriHandler.openUri(
                                        if (name.first() > '1')
                                            "https://github.com/Kiteio/Punica-CMP/releases/tag/$tag"
                                        else
                                            "https://github.com/Kiteio/Punica/releases/tag/$tag",
                                    )
                                }
                            ) {
                                Text(stringResource(Res.string.github))
                            }
                        },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                // 资产
                items(assets.filter { it.name.startsWith("punica") }) {
                    CardListItem(
                        headlineContent = { Text(it.name) },
                        onClick = { uriHandler.openUri(it.urlString) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                // 版本描述
                item {
                    Markdown(description)
                }
            }
        }
    }
}