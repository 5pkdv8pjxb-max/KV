package com.example.kursovik.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [RateEntity::class, FavoriteEntity::class, HistoryEntity::class],
    version = 3,
    exportSchema = false
)
abstract class KursDatabase : RoomDatabase() {
    abstract fun rateDao(): RateDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun historyDao(): HistoryDao
}