package org.kiteio.punica.mirror.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.kiteio.punica.mirror.modal.User

/**
 * [User] Dao。
 */
@Dao
interface UserDao {
    /**
     * 插入 [user]。
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg user: User)

    /**
     * 通过 [id] 获取 [User]。
     */
    @Query("SELECT * FROM user WHERE id = :id")
    suspend fun getById(id: String): User?

    /**
     * 获取所有 [User] 的 [Flow]。
     */
    @Query("SELECT * FROM user")
    fun getAllFlow(): Flow<List<User>>
}