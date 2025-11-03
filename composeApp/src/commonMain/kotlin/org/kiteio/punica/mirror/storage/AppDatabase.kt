package org.kiteio.punica.mirror.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import jakarta.inject.Singleton
import org.kiteio.punica.mirror.modal.User
import org.kiteio.punica.mirror.storage.dao.UserDao
import org.kiteio.punica.mirror.util.AppDirs

/**
 * 返回 [AppDatabase]。
 */
@Singleton
fun getAppDatabase(): AppDatabase {
    val path = AppDirs.filesDir("database/database.db")

    return buildAppDatabase(path) {
        setDriver(BundledSQLiteDriver())
    }
}

/**
 * 数据库。
 */
@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

/**
 * 构建 [AppDatabase]。
 *
 * @param path 数据库存储路径
 */
expect inline fun buildAppDatabase(
    path: String,
    block: RoomDatabase.Builder<AppDatabase>.() -> Unit,
): AppDatabase