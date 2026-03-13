package com.buidlstack.stacksubil.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.buidlstack.stacksubil.data.db.dao.HistoryDao
import com.buidlstack.stacksubil.data.db.dao.MeasurementDao
import com.buidlstack.stacksubil.data.db.dao.ProjectDao
import com.buidlstack.stacksubil.data.db.entity.HistoryEntity
import com.buidlstack.stacksubil.data.db.entity.MeasurementEntity
import com.buidlstack.stacksubil.data.db.entity.ProjectEntity

@Database(
    entities = [MeasurementEntity::class, ProjectEntity::class, HistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class QuantiaDatabase : RoomDatabase() {

    abstract fun measurementDao(): MeasurementDao
    abstract fun projectDao(): ProjectDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: QuantiaDatabase? = null

        fun getDatabase(context: Context): QuantiaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuantiaDatabase::class.java,
                    "quantia_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
