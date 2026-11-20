package com.example.kursovik.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rates")
data class RateEntity(
    @PrimaryKey
    val charCode: String,
    val name: String,
    val nominal: Int,
    val value: Double,
    val valuePerUnit: Double,
    val date: String,
    @androidx.room.ColumnInfo(defaultValue = "''")
    val cbrId: String = "",
    @androidx.room.ColumnInfo(defaultValue = "0")
    val updatedAt: Long = 0L
)