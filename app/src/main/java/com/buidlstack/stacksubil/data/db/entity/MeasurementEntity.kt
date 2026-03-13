package com.buidlstack.stacksubil.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "measurements")
data class MeasurementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val value: Double,
    val unit: String,
    val projectId: Long? = null,
    val category: String = "",
    val note: String = "",
    val photoUri: String = "",
    val date: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
