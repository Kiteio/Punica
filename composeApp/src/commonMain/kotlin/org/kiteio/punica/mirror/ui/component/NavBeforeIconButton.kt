package org.kiteio.punica.mirror.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.NavigateBefore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.mirror.ui.navigation.navigateUp
import org.koin.compose.koinInject
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.navigate_before

/**
 * 返回按钮。
 */
@Composable
fun NavBeforeIconButton() {
    val backStack = koinInject<NavBackStack<NavKey>>()

    IconButton(onClick = { backStack.navigateUp() }) {
        Icon(
            Icons.AutoMirrored.Outlined.NavigateBefore,
            contentDescription = stringResource(Res.string.navigate_before),
        )
    }
}