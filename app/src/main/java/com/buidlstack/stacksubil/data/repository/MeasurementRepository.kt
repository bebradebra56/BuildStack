package com.buidlstack.stacksubil.data.repository

import com.buidlstack.stacksubil.data.db.dao.MeasurementDao
import com.buidlstack.stacksubil.data.db.entity.MeasurementEntity
import kotlinx.coroutines.flow.Flow

class MeasurementRepository(private val dao: MeasurementDao) {

    fun getAllMeasurements(): Flow<List<MeasurementEntity>> = dao.getAllMeasurements()

    fun getFavorites(): Flow<List<MeasurementEntity>> = dao.getFavorites()

    fun getMeasurementsByProject(projectId: Long): Flow<List<MeasurementEntity>> =
        dao.getMeasurementsByProject(projectId)

    fun getRecentMeasurements(limit: Int = 5): Flow<List<MeasurementEntity>> =
        dao.getRecentMeasurements(limit)

    suspend fun getMeasurementById(id: Long): MeasurementEntity? = dao.getMeasurementById(id)

    suspend fun insert(measurement: MeasurementEntity): Long = dao.insert(measurement)

    suspend fun update(measurement: MeasurementEntity) = dao.update(measurement)

    suspend fun delete(measurement: MeasurementEntity) = dao.delete(measurement)

    suspend fun deleteById(id: Long) = dao.deleteById(id)
}
