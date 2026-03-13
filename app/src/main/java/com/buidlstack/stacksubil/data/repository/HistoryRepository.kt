package com.buidlstack.stacksubil.data.repository

import com.buidlstack.stacksubil.data.db.dao.HistoryDao
import com.buidlstack.stacksubil.data.db.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val dao: HistoryDao) {

    fun getAllHistory(): Flow<List<HistoryEntity>> = dao.getAllHistory()

    fun getRecentHistory(limit: Int = 20): Flow<List<HistoryEntity>> = dao.getRecentHistory(limit)

    suspend fun addHistory(entity: HistoryEntity) = dao.insert(entity)

    suspend fun clearAll() = dao.clearAll()
}
