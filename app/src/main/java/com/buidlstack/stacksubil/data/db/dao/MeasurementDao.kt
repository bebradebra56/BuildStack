package com.buidlstack.stacksubil.data.db.dao

import androidx.room.*
import com.buidlstack.stacksubil.data.db.entity.MeasurementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {

    @Query("SELECT * FROM measurements ORDER BY date DESC")
    fun getAllMeasurements(): Flow<List<MeasurementEntity>>

    @Query("SELECT * FROM measurements WHERE isFavorite = 1 ORDER BY date DESC")
    fun getFavorites(): Flow<List<MeasurementEntity>>

    @Query("SELECT * FROM measurements WHERE projectId = :projectId ORDER BY date DESC")
    fun getMeasurementsByProject(projectId: Long): Flow<List<MeasurementEntity>>

    @Query("SELECT * FROM measurements WHERE id = :id")
    suspend fun getMeasurementById(id: Long): MeasurementEntity?

    @Query("SELECT * FROM measurements ORDER BY date DESC LIMIT :limit")
    fun getRecentMeasurements(limit: Int): Flow<List<MeasurementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(measurement: MeasurementEntity): Long

    @Update
    suspend fun update(measurement: MeasurementEntity)

    @Delete
    suspend fun delete(measurement: MeasurementEntity)

    @Query("DELETE FROM measurements WHERE id = :id")
    suspend fun deleteById(id: Long)
}
