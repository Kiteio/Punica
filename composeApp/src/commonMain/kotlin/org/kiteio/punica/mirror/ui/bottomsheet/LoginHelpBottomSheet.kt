package org.kiteio.punica.mirror.ui.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import punica.composeapp.generated.resources.*

/**
 * 登录帮助底部抽屉。
 */
@Composable
fun LoginHelpBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
) {
    BottomSheet(
        visible = visible,
        onDismissRequest = onDismissRequest,
    ) {
        // 数据安全
        Description(
            title = stringResource(Res.string.data_secure),
            icon = Icons.Outlined.Storage,
        ) {
            Text(stringResource(Res.string.desc_data_secure))
        }
        Spacer(Modifier.height(24.dp))

        // 自动登录
        Description(
            title = stringResource(Res.string.auto_login),
            icon = Icons.Outlined.Autorenew,
        ) {
            Text(stringResource(Res.string.desc_auto_login))
        }
        Spacer(Modifier.height(24.dp))

        // 忘记密码
        Description(
            title = stringResource(Res.string.forget_password),
            icon = Icons.Outlined.Password,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(Res.string.desc_forget_password))
            Spacer(Modifier.height(16.dp))
            // 前往重置按钮
            OutlinedButton(
                onClick = {
                    // TODO: 打开 Uri
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.OpenInNew,
                    contentDescription = stringResource(Res.string.go_to_reset),
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(Res.string.go_to_reset))
            }
        }
    }
}

/**
 * 描述。
 *
 * @param title 标题
 * @param icon 图标
 */
@Composable
private fun Description(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier) {
        // 标题
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 图标
            Icon(
                icon,
                contentDescription = title,
            )

            // 标题文字
            Text(
                title,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.height(8.dp))

        // 内容
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.bodySmall,
        ) {
            content()
        }
    }
}