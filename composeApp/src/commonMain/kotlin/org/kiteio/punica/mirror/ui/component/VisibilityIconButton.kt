package org.kiteio.punica.mirror.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.mirror.ui.theme.PunicaExpressiveTheme
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.visibility
import punica.composeapp.generated.resources.visibility_off

@Preview
@Composable
private fun VisibilityIconButtonPreview() {
    PunicaExpressiveTheme {
        Column {
            VisibilityIconButton(
                visible = false,
                onVisibleChange = { },
            )
            VisibilityIconButton(
                visible = true,
                onVisibleChange = { },
            )
        }
    }
}

/**
 * 可见图标按钮。
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun VisibilityIconButton(
    visible: Boolean,
    onVisibleChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    IconToggleButton(
        checked = visible,
        onCheckedChange = onVisibleChange,
        modifier = modifier,
    ) {
        Icon(
            with(Icons.Outlined) {
                if (visible) Visibility else VisibilityOff
            },
            contentDescription = stringResource(
                with(Res.string) {
                    if (visible) visibility else visibility_off
                }
            ),
        )
    }
}