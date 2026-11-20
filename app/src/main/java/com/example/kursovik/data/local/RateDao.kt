package com.example.kursovik.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RateDao {

    @Query("SELECT * FROM rates")
    fun getAllRates(): Flow<List<RateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rates: List<RateEntity>)

    @Transaction
    suspend fun replaceAll(rates: List<RateEntity>) {
        clearAll()
        insertAll(rates)
    }

    @Query("DELETE FROM rates")
    suspend fun clearAll()
}