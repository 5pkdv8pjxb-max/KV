package com.example.kursovik.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(fav: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE charCode = :code")
    suspend fun removeFavorite(code: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE charCode = :code)")
    suspend fun isFavorite(code: String): Boolean
}