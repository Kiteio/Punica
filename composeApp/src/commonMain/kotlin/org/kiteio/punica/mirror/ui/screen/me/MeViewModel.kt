package org.kiteio.punica.mirror.ui.screen.me

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.kiteio.punica.mirror.modal.Gender
import org.kiteio.punica.mirror.modal.education.Campus
import org.kiteio.punica.mirror.repository.WallpaperRepository
import org.kiteio.punica.mirror.storage.Preferences
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MeViewModel(
    private val wallpaperRepository: WallpaperRepository,
) : ViewModel() {
    /** 壁纸 */
    val wallpaper = flow {
        emit(wallpaperRepository.getWallpaper()).also {
            _isWallpaperLoading.update { false }
        }
    }.catch {
        _isWallpaperLoading.update { false }
    }

    private var _isWallpaperLoading = MutableStateFlow(true)

    /** 壁纸是否正在加载 */
    val isWallpaperLoading = _isWallpaperLoading.asStateFlow()

    /** 校区 */
    val campus = Preferences.campus

    /** 周次 */
    val week = Preferences.week

    /** 主色调 */
    val primaryColor = Preferences.primaryColor

    /** 性别 */
    val gender = Preferences.gender


    /**
     * 更改校区。
     */
    fun changeCampus(campus: Campus) {
        viewModelScope.launch {
            Preferences.changeCampus(campus)
        }
    }

    /**
     * 更改周次。
     */
    fun changeWeek(week: Int) {
        viewModelScope.launch {
            Preferences.changeWeek(week)
        }
    }

    /**
     * 更改主色调。
     */
    fun changePrimaryColor(color: Color) {
        viewModelScope.launch {
            Preferences.changePrimaryColor(color)
        }
    }

    /**
     * 更改性别。
     */
    fun changeGender(gender: Gender) {
        viewModelScope.launch {
            Preferences.changeGender(gender)
        }
    }
}