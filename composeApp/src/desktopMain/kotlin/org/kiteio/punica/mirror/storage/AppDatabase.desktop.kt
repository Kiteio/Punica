package org.kiteio.punica.mirror.storage

import androidx.room.Room
import androidx.room.RoomDatabase

actual inline fun buildAppDatabase(
    path: String,
    block: RoomDatabase.Builder<AppDatabase>.() -> Unit,
): AppDatabase {
    return Room.databaseBuilder<AppDatabase>(path)
        .apply(block)
        .build()
}