package org.kiteio.punica.ui.page.cet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.client.cet.CET
import org.kiteio.punica.client.cet.api.CETExam
import org.kiteio.punica.client.cet.api.getExam
import org.kiteio.punica.ui.page.modules.ModuleRoute
import org.kiteio.punica.ui.component.LoadingNotNullOrEmpty
import org.kiteio.punica.ui.component.NavBackAppBar
import org.kiteio.punica.wrapper.launchCatching
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.cet

/**
 * 四六级页面路由。
 */
@Serializable
object CETRoute : ModuleRoute {
    override val nameRes = Res.string.cet
    override val icon = Icons.Outlined.Verified
}


/**
 * 四六级页面。
 */
@Composable
fun CETPage() = Content()


@Composable
private fun Content() {
    var isLoading by remember { mutableStateOf(true) }
    val exam by produceState<CETExam?>(null) {
        launchCatching {
            try {
                value = CET().getExam()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = { NavBackAppBar(title = { Text(stringResource(CETRoute.nameRes)) }) },
    ) { innerPadding ->
        LoadingNotNullOrEmpty(
            exam,
            isLoading = isLoading,
            modifier = Modifier.padding(innerPadding)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(it.time)
                Spacer(modifier = Modifier.height(16.dp))
                Text(it.note, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}