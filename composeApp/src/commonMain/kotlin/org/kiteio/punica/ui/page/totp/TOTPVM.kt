package org.kiteio.punica.ui.page.totp

import androidx.lifecycle.ViewModel
import org.kiteio.punica.serialization.Stores
import org.kiteio.punica.serialization.deserializeToList
import org.kiteio.punica.tool.TOTPUser

class TOTPVM: ViewModel() {
    val tOTPUsersFlow = Stores.tOTPUsers.data.deserializeToList<TOTPUser>()
}