package org.kiteio.punica.ui.widget

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.ui.compositionlocal.LocalNavController
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.back

/**
 * 带有返回按钮的 [TopAppBar]。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavBackAppBar(
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val navController = LocalNavController.current

    TopAppBar(
        title = title,
        navigationIcon = {
            // 返回按钮
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Outlined.ArrowBackIosNew,
                    contentDescription = stringResource(Res.string.back),
                )
            }
        },
        actions = actions,
    )
}