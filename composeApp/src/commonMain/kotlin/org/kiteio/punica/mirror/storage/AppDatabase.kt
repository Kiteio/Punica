package org.kiteio.punica.mirror.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import jakarta.inject.Singleton
import org.kiteio.punica.mirror.modal.User
import org.kiteio.punica.mirror.modal.cet.CetExam
import org.kiteio.punica.mirror.storage.dao.CetExamDao
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
@Database(entities = [User::class, CetExam::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    abstract fun cetExamDao(): CetExamDao
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