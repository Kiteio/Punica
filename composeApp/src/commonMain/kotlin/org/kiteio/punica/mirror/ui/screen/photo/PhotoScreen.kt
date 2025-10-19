package org.kiteio.punica.mirror.ui.screen.photo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.kiteio.punica.mirror.ui.navigation.BottomNavKey
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.photo

/**
 * 相册页入口。
 */
fun EntryProviderBuilder<NavKey>.photoEntry() {
    entry<PhotoRoute> { PhotoScreen() }
}

/**
 * 相册页路由。
 */
@Serializable
data object PhotoRoute : BottomNavKey {
    override val strRes = Res.string.photo
    override val icon = Icons.Outlined.Photo
    override val selectedIcon = Icons.Filled.Photo
}

@Composable
private fun PhotoScreen() {

}