package org.kiteio.punica.mirror.repository

import jakarta.inject.Singleton
import org.kiteio.punica.mirror.modal.bing.Wallpaper
import org.kiteio.punica.mirror.service.BingService

/**
 * 壁纸存储库。
 *
 * @param service Bing 服务
 */
@Singleton
fun getWallpaperRepository(
    service: BingService,
): WallpaperRepository {
    return WallpaperRepositoryImpl(service)
}

/**
 * 壁纸存储库。
 */
interface WallpaperRepository {
    /**
     * 获取壁纸。
     */
    suspend fun getWallpaper(): Wallpaper
}

// --------------- 实现 ---------------

private class WallpaperRepositoryImpl(
    private val service: BingService,
) : WallpaperRepository {
    override suspend fun getWallpaper(): Wallpaper {
        return service.getWallpapers().first()
    }
}