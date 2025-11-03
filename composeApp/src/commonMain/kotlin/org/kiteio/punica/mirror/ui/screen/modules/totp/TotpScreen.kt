package org.kiteio.punica.mirror.ui.screen.modules.totp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Security
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.kiteio.punica.mirror.ui.navigation.ModuleNavKey
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.otp

/**
 * OTP 页入口。
 */
fun EntryProviderScope<NavKey>.totpEntry() {
    entry<TotpRoute> { TotpScreen() }
}

/**
 * OTP 页
 */
@Serializable
data object TotpRoute : ModuleNavKey {
    override val strRes = Res.string.otp
    override val icon = Icons.Outlined.Security
}

@Composable
private fun TotpScreen() {
}