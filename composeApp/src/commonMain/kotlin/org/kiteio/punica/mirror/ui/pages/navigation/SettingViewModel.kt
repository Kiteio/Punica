package org.kiteio.punica.mirror.ui.pages.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kiteio.punica.mirror.repository.WallpaperRepository
import org.kiteio.punica.mirror.service.BingService
import org.kiteio.punica.mirror.ui.MVI
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** 设置页面 Koin 模块 */
val settingModule = module {
    singleOf(::BingService)
    singleOf(::WallpaperRepository)
    viewModelOf(::SettingViewModel)
}

/**
 * 设置页面 ViewModel。
 */
class SettingViewModel(
    private val wallpaperRepository: WallpaperRepository,
) : ViewModel(), MVI<SettingUiState, SettingIntent> {
    private val _uiState = MutableStateFlow<SettingUiState>(SettingUiState.Loading)
    override val uiState = _uiState.asStateFlow()

    override fun dispatch(intent: SettingIntent) {
        when (intent) {
            SettingIntent.LoadWallpaper -> loadWallpaper()
        }
    }

    /**
     * 加载壁纸。
     */
    private fun loadWallpaper() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                try {
                    val wallpaper = wallpaperRepository.getWallpaper()
                    _uiState.emit(SettingUiState.Success(wallpaper))
                } catch (e: Exception) {
                    _uiState.emit(SettingUiState.Error(e))
                }
            }
        }
    }
}

/**
 * 设置页面意图。
 */
sealed class SettingIntent {
    /** 加载壁纸 */
    object LoadWallpaper : SettingIntent()
}

/**
 * 设置页面 UI 状态。
 */
sealed class SettingUiState {
    /** 加载中 */
    data object Loading : SettingUiState()

    /**
     * 加载成功。
     *
     * @property wallpaperUrl 壁纸 url
     */
    data class Success(
        val wallpaperUrl: String,
    ) : SettingUiState()

    /** 加载失败 */
    data class Error(val e: Throwable) : SettingUiState()
}