package org.kiteio.punica.ui.page.totp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.serialization.Stores
import org.kiteio.punica.serialization.set
import org.kiteio.punica.tool.TOTPUser
import org.kiteio.punica.ui.component.ElevatedPasswordTextField
import org.kiteio.punica.ui.component.ElevatedTextField
import org.kiteio.punica.ui.component.ModalBottomSheet
import org.kiteio.punica.ui.component.showToast
import org.kiteio.punica.wrapper.focusCleaner
import org.kiteio.punica.wrapper.launchCatching
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.error_name_is_empty
import punica.composeapp.generated.resources.error_secret_is_empty
import punica.composeapp.generated.resources.name
import punica.composeapp.generated.resources.save
import punica.composeapp.generated.resources.save_successful
import punica.composeapp.generated.resources.secret
import punica.composeapp.generated.resources.tip_how_to_get_secret

@Composable
fun TOTPUserBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    tOTPUser: TOTPUser?,
) {
    ModalBottomSheet(visible, onDismissRequest) {
        val focusManager = LocalFocusManager.current
        val scope = rememberCoroutineScope()
        var name by remember { mutableStateOf(tOTPUser?.name ?: "") }
        var secret by remember { mutableStateOf(tOTPUser?.secret ?: "") }
        var isSaveButtonClicked by remember { mutableStateOf(false) }

        LazyColumn(
            modifier = Modifier.fillMaxSize().focusCleaner(focusManager),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            contentPadding = PaddingValues(32.dp),
        ) {
            item {
                ElevatedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(0.9f),
                    readOnly = tOTPUser != null,
                    label = { Text(stringResource(Res.string.name)) },
                    placeholder = { Text(stringResource(Res.string.name)) },
                    isError = isSaveButtonClicked && secret.isBlank(),
                    errorText = {
                        Text(stringResource(Res.string.error_name_is_empty))
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                ElevatedPasswordTextField(
                    value = secret,
                    onValueChange = {
                        val regex = Regex("^otpauth://totp/([^?]+).*[?&]secret=([^&]*)")
                        val matchResult = regex.find(it)
                        if (matchResult != null) {
                            name = matchResult.groupValues[1]
                            secret = matchResult.groupValues[2]
                        } else {
                            secret = it
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.9f),
                    label = { Text(stringResource(Res.string.secret)) },
                    placeholder = { Text(stringResource(Res.string.secret)) },
                    isError = isSaveButtonClicked && secret.isBlank(),
                    errorText = {
                        Text(stringResource(Res.string.error_secret_is_empty))
                    },
                    supportingText = {
                        Text(stringResource(Res.string.tip_how_to_get_secret))
                    },
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                ElevatedButton(
                    onClick = {
                        scope.launchCatching {
                            require(name.isNotBlank()) {
                                showToast(getString(Res.string.error_name_is_empty))
                            }

                            require(secret.isNotBlank()) {
                                showToast(getString(Res.string.error_secret_is_empty))
                            }

                            Stores.tOTPUsers.edit {
                                it[name] = TOTPUser(name, secret)
                            }
                            showToast(getString(Res.string.save_successful))
                            onDismissRequest()
                        }
                    },
                ) {
                    Text(stringResource(Res.string.save))
                }
            }
        }
    }
}