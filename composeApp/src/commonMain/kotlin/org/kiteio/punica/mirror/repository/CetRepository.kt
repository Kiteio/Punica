package org.kiteio.punica.mirror.repository

import jakarta.inject.Singleton
import kotlinx.coroutines.flow.first
import org.kiteio.punica.mirror.modal.cet.CetExam
import org.kiteio.punica.mirror.service.CetService
import org.kiteio.punica.mirror.storage.AppDatabase

/**
 * 四六级存储库。
 */
@Singleton
fun getCetRepository(
    service: CetService,
    database: AppDatabase,
): CetRepository {
    return CetRepositoryImpl(service, database)
}

/**
 * 四六级存储库。
 */
interface CetRepository {
    /**
     * 获取 Cet 考试。
     */
    suspend fun getExam(): CetExam
}

// --------------- 实现 ---------------

private class CetRepositoryImpl(
    private val service: CetService,
    database: AppDatabase,
) : CetRepository {
    private val cetExamDao = database.cetExamDao()

    override suspend fun getExam(): CetExam {
        return try {
            service.getExam().also {
                // 保存本地
                cetExamDao.insert(it)
            }
        } catch (e: Exception) {
            cetExamDao.getAllFlow()
                .first().firstOrNull() ?: throw e
        }
    }
}