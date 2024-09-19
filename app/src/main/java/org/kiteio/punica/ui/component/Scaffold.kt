package org.kiteio.punica.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.kiteio.punica.ui.focusCleaner

/**
 * [Scaffold] 嵌套 [Column]
 * @param modifier
 * @param innerModifier
 * @param verticalArrangement
 * @param horizontalAlignment
 * @param topBar
 * @param bottomBar
 * @param floatingActionButton
 * @param contentWindowInsets
 * @param content
 */
@Composable
fun ScaffoldColumn(
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    contentWindowInsets: WindowInsets = WindowInsets.statusBars,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        modifier = modifier.focusCleaner(),
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        contentWindowInsets = contentWindowInsets
    ) { innerPadding ->
        Column(
            modifier = innerModifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            content = content
        )
    }
}


/**
 * [Scaffold] 嵌套 [Box]
 * @param modifier
 * @param innerModifier
 * @param contentAlignment
 * @param topBar
 * @param bottomBar
 * @param floatingActionButton
 * @param contentWindowInsets
 * @param content
 */
@Composable
fun ScaffoldBox(
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    contentWindowInsets: WindowInsets = WindowInsets.statusBars,
    content: @Composable BoxScope.() -> Unit
) {
    Scaffold(
        modifier = modifier.focusCleaner(),
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        contentWindowInsets = contentWindowInsets
    ) { innerPadding ->
        Box(
            modifier = innerModifier.fillMaxSize().padding(innerPadding),
            contentAlignment = contentAlignment,
            content = content
        )
    }
}