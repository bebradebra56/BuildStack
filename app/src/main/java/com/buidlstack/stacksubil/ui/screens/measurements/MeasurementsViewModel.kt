package com.buidlstack.stacksubil.ui.screens.measurements

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.buidlstack.stacksubil.data.db.entity.HistoryEntity
import com.buidlstack.stacksubil.data.db.entity.MeasurementEntity
import com.buidlstack.stacksubil.data.db.entity.ProjectEntity
import com.buidlstack.stacksubil.grfed.presentation.app.BuildStackApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MeasurementsViewModel(app: Application) : AndroidViewModel(app) {

    private val measurementRepo = (app as BuildStackApplication).measurementRepository
    private val projectRepo = (app as BuildStackApplication).projectRepository
    private val historyRepo = (app as BuildStackApplication).historyRepository

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _projectFilter = MutableStateFlow<Long?>( null)
    val projectFilter: StateFlow<Long?> = _projectFilter

    private val _unitFilter = MutableStateFlow<String?>( null)
    val unitFilter: StateFlow<String?> = _unitFilter

    val projects: StateFlow<List<ProjectEntity>> = projectRepo.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val measurements: StateFlow<List<MeasurementEntity>> = combine(
        _searchQuery, _projectFilter, _unitFilter
    ) { query, projectId, unit ->
        Triple(query, projectId, unit)
    }.flatMapLatest { (query, projectId, unit) ->
        when {
            projectId != null -> measurementRepo.getMeasurementsByProject(projectId)
            else -> measurementRepo.getAllMeasurements()
        }.map { list ->
            list.filter { m ->
                (query.isEmpty() || m.title.contains(query, ignoreCase = true)) &&
                (unit == null || m.unit == unit)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<MeasurementEntity>> = measurementRepo.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setProjectFilter(id: Long?) { _projectFilter.value = id }
    fun setUnitFilter(unit: String?) { _unitFilter.value = unit }

    fun addMeasurement(measurement: MeasurementEntity, onResult: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = measurementRepo.insert(measurement)
            historyRepo.addHistory(
                HistoryEntity(
                    action = "Added measurement",
                    entityType = "measurement",
                    entityId = id,
                    entityTitle = measurement.title
                )
            )
            onResult(id)
        }
    }

    fun updateMeasurement(measurement: MeasurementEntity) {
        viewModelScope.launch {
            measurementRepo.update(measurement)
            historyRepo.addHistory(
                HistoryEntity(
                    action = "Updated measurement",
                    entityType = "measurement",
                    entityId = measurement.id,
                    entityTitle = measurement.title
                )
            )
        }
    }

    fun deleteMeasurement(measurement: MeasurementEntity) {
        viewModelScope.launch {
            measurementRepo.delete(measurement)
            historyRepo.addHistory(
                HistoryEntity(
                    action = "Deleted measurement",
                    entityType = "measurement",
                    entityId = measurement.id,
                    entityTitle = measurement.title
                )
            )
        }
    }

    fun toggleFavorite(measurement: MeasurementEntity) {
        viewModelScope.launch {
            measurementRepo.update(measurement.copy(isFavorite = !measurement.isFavorite))
        }
    }

    fun duplicateMeasurement(measurement: MeasurementEntity) {
        viewModelScope.launch {
            val copy = measurement.copy(
                id = 0,
                title = "${measurement.title} (copy)",
                date = System.currentTimeMillis()
            )
            val id = measurementRepo.insert(copy)
            historyRepo.addHistory(
                HistoryEntity(
                    action = "Duplicated measurement",
                    entityType = "measurement",
                    entityId = id,
                    entityTitle = copy.title
                )
            )
        }
    }

    suspend fun getMeasurementById(id: Long): MeasurementEntity? =
        measurementRepo.getMeasurementById(id)
}
