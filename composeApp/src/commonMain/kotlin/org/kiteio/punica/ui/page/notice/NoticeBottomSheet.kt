package org.kiteio.punica.ui.page.notice

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.bodyAsText
import org.kiteio.punica.client.office.api.Notice
import org.kiteio.punica.http.Client
import org.kiteio.punica.ui.component.LoadingNotNullOrEmpty
import org.kiteio.punica.ui.component.ModalBottomSheet
import org.kiteio.punica.wrapper.launchCatching

/**
 * 通知详情。
 */
@Composable
fun NoticeBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    notice: Notice?,
) {
    ModalBottomSheet(visible, onDismissRequest) {
        notice?.run {
            var isLoading by remember { mutableStateOf(false) }
            val html by produceState<String?>(null) {
                launchCatching {
                    try {
                        val text = Client("").get(urlString).bodyAsText()
                        val doc = Ksoup.parse(text).getElementById("d-container")!!

                        value = doc.html()
                    } finally {
                        isLoading = false
                    }
                }
            }

            LoadingNotNullOrEmpty(html, isLoading = isLoading) {
                val htmlText = remember(it) { htmlToAnnotatedString(it) }

                LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    item { Text(htmlText, lineHeight = 32.sp) }
                }
            }
        }
    }
}