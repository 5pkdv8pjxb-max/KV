package com.example.kursovik.data.di

import android.content.Context
import androidx.room.Room
import com.example.kursovik.data.local.FavoriteDao
import com.example.kursovik.data.local.HistoryDao
import com.example.kursovik.data.local.KursDatabase
import com.example.kursovik.data.local.RateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATABASE_NAME = "kurs_database"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideKursDatabase(@ApplicationContext context: Context): KursDatabase {
        return Room.databaseBuilder(
            context,
            KursDatabase::class.java,
            DATABASE_NAME
        ).addMigrations(object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE rates ADD COLUMN cbrId TEXT NOT NULL DEFAULT ''")
            }
        }, object : androidx.room.migration.Migration(2, 3) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE rates ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
            }
        }).build()
    }

    @Provides
    @Singleton
    fun provideRateDao(database: KursDatabase): RateDao = database.rateDao()

    @Provides
    @Singleton
    fun provideFavoriteDao(database: KursDatabase): FavoriteDao = database.favoriteDao()

    @Provides
    @Singleton
    fun provideHistoryDao(database: KursDatabase): HistoryDao = database.historyDao()
}