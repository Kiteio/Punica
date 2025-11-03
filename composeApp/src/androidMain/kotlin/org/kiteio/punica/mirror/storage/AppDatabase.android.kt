package org.kiteio.punica.mirror.storage

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.java.KoinJavaComponent.inject

actual inline fun buildAppDatabase(
    path: String,
    block: RoomDatabase.Builder<AppDatabase>.() -> Unit,
): AppDatabase {
    val context by inject<Context>(Context::class.java)
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        path,
    ).apply(block).build()
}