package org.kiteio.punica.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.kiteio.punica.ui.dp4


/**
 * 加载中提示
 */
@Composable
fun LinearProgressIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dp4(6)),
        contentAlignment = Alignment.Center
    ) { LinearProgressIndicator() }
}