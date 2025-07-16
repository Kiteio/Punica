package org.kiteio.punica.mirror.repository

import org.kiteio.punica.mirror.service.BingService

/**
 * 壁纸存储库。
 *
 * @param service Bing 服务
 */
fun WallpaperRepository(
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
    suspend fun getWallpaper(): String
}

// --------------- 实现 ---------------

private class WallpaperRepositoryImpl(
    private val service: BingService,
) : WallpaperRepository {
    override suspend fun getWallpaper(): String {
        return service.getWallpapers().first().url
    }
}