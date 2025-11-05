package org.kiteio.punica.mirror.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.kiteio.punica.mirror.modal.cet.CetExam

/**
 * [CetExam] Dao。
 */
@Dao
interface CetExamDao {
    /**
     * 插入 [cetExam]。
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg cetExam: CetExam)

    /**
     * 获取所有 [CetExam] 的 [Flow]。
     */
    @Query("SELECT * FROM cet_exam")
    fun getAllFlow(): Flow<List<CetExam>>
}