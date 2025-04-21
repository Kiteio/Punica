package org.kiteio.punica.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.invisible
import punica.composeapp.generated.resources.visible

@Composable
fun ElevatedPasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorText: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
) {
    var visible by remember { mutableStateOf(false) }

    ElevatedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        readOnly = readOnly,
        label = label,
        placeholder = placeholder,
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    if (visible) Icons.Outlined.Visibility else
                        Icons.Outlined.VisibilityOff,
                    stringResource(
                        if (visible) Res.string.visible else
                            Res.string.invisible,
                    ),
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else
            remember { PasswordVisualTransformation() },
        isError = isError,
        errorText = errorText,
        supportingText = supportingText,
    )
}


@Composable
fun ElevatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean = false,
    errorText: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
) {
    Column(modifier = modifier) {
        if (label != null) {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.primary,
                    LocalTextStyle provides MaterialTheme.typography.labelSmall,
                ) {
                    label()
                }
            }
        }
        Surface(
            shape = CircleShape,
            tonalElevation = 0.5.dp,
            shadowElevation = 1.dp,
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                readOnly = readOnly,
                placeholder = placeholder?.let { { placeholder() } },
                trailingIcon = trailingIcon,
                visualTransformation = visualTransformation,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    errorBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                ),
            )
        }
        if (isError && errorText != null) {
            Box(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 8.dp)) {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.error,
                    LocalTextStyle provides MaterialTheme.typography.labelSmall,
                ) {
                    errorText()
                }
            }
        }
        if (supportingText != null) {
            Box(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 8.dp)) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.labelSmall,
                ) {
                    supportingText()
                }
            }
        }
    }
}