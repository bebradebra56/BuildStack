package com.buidlstack.stacksubil.ui.screens.projects

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

class ProjectsViewModel(app: Application) : AndroidViewModel(app) {

    private val projectRepo = (app as BuildStackApplication).projectRepository
    private val measurementRepo = (app as BuildStackApplication).measurementRepository
    private val historyRepo = (app as BuildStackApplication).historyRepository

    val projects: StateFlow<List<ProjectEntity>> = projectRepo.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getMeasurementsForProject(projectId: Long): Flow<List<MeasurementEntity>> =
        measurementRepo.getMeasurementsByProject(projectId)

    fun addProject(project: ProjectEntity, onResult: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = projectRepo.insert(project)
            historyRepo.addHistory(
                HistoryEntity(
                    action = "Created project",
                    entityType = "project",
                    entityId = id,
                    entityTitle = project.name
                )
            )
            onResult(id)
        }
    }

    fun updateProject(project: ProjectEntity) {
        viewModelScope.launch {
            projectRepo.update(project.copy(lastUpdated = System.currentTimeMillis()))
            historyRepo.addHistory(
                HistoryEntity(
                    action = "Updated project",
                    entityType = "project",
                    entityId = project.id,
                    entityTitle = project.name
                )
            )
        }
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch {
            projectRepo.delete(project)
            historyRepo.addHistory(
                HistoryEntity(
                    action = "Deleted project",
                    entityType = "project",
                    entityId = project.id,
                    entityTitle = project.name
                )
            )
        }
    }

    suspend fun getProjectById(id: Long): ProjectEntity? = projectRepo.getProjectById(id)
}
