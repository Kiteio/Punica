package org.kiteio.punica.mirror.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.kiteio.punica.mirror.storage.Preferences
import org.kiteio.punica.mirror.ui.theme.ThemeMode
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SettingsViewModel: ViewModel() {
    /** 主题模式 */
    val themeMode = Preferences.themeMode

    /**
     * 更改主题模式。
     */
    fun changeThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            Preferences.changeTheme(themeMode)
        }
    }
}