package org.kiteio.punica.mirror.repository

import jakarta.inject.Singleton
import org.kiteio.punica.mirror.service.NoticeService

/**
 * 教学通知详情存储库。
 */
@Singleton
fun getNoticeDetailRepository(
    service: NoticeService,
): NoticeDetailRepository {
    return NoticeDetailRepositoryImpl(service)
}

/**
 * 教学通知详情存储库。
 */
interface NoticeDetailRepository {
    /**
     * 获取教学通知详情 Html。
     */
    suspend fun getNoticeDetailHtml(urlString: String): String
}

// --------------- 实现 ---------------

class NoticeDetailRepositoryImpl(
    private val service: NoticeService,
) : NoticeDetailRepository {
    override suspend fun getNoticeDetailHtml(urlString: String): String {
        return service.getNotice(urlString)
    }
}