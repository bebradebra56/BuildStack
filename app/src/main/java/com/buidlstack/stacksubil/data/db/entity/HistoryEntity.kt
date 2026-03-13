package com.buidlstack.stacksubil.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val action: String,
    val entityType: String,
    val entityId: Long = 0,
    val entityTitle: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
