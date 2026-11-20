package com.example.kursovik.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fromCode: String,
    val toCode: String,
    val amount: Double,
    val result: Double,
    val timestamp: Long
)