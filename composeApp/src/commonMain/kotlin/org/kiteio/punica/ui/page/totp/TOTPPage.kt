package org.kiteio.punica.ui.page.totp

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.tool.TOTPUser
import org.kiteio.punica.ui.component.LoadingNotNullOrEmpty
import org.kiteio.punica.ui.component.NavBackAppBar
import org.kiteio.punica.ui.page.modules.ModuleRoute
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.add
import punica.composeapp.generated.resources.otp

/**
 * OTP 页面路由。
 */
@Serializable
object TOTPRoute : ModuleRoute {
    override val nameRes = Res.string.otp
    override val icon = Icons.Outlined.Security
}


/**
 * OTP 页面。
 */
@Composable
fun TOTPPage() = viewModel { TOTPVM() }.Content()


@Composable
private fun TOTPVM.Content() {
    val tOTPUsers by tOTPUsersFlow.collectAsState(emptyList())
    var tOTPUserBottomSheetVisible by remember { mutableStateOf(false) }
    var tOTPUser by remember { mutableStateOf<TOTPUser?>(null) }

    Scaffold(
        topBar = {
            NavBackAppBar(
                title = { Text(stringResource(TOTPRoute.nameRes)) },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { tOTPUserBottomSheetVisible = true },
            ) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = stringResource(Res.string.add),
                )
            }
        },
    ) { innerPadding ->
        LoadingNotNullOrEmpty(
            tOTPUsers,
            isLoading = false,
            modifier = Modifier.padding(innerPadding),
        ) { totpUsers ->
            LazyVerticalGrid(
                columns = GridCells.Adaptive(232.dp),
                contentPadding = PaddingValues(8.dp),
            ) {
                items(totpUsers) {
                    TOTPUser(
                        it,
                        onClick = {
                            tOTPUser = it
                            tOTPUserBottomSheetVisible = true
                        },
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }

    TOTPUserBottomSheet(
        tOTPUserBottomSheetVisible,
        onDismissRequest = {
            tOTPUserBottomSheetVisible = false
            tOTPUser = null
        },
        tOTPUser = tOTPUser,
    )
}