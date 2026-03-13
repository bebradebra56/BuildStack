package com.buidlstack.stacksubil.data.repository

import com.buidlstack.stacksubil.data.db.dao.ProjectDao
import com.buidlstack.stacksubil.data.db.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val dao: ProjectDao) {

    fun getAllProjects(): Flow<List<ProjectEntity>> = dao.getAllProjects()

    suspend fun getProjectById(id: Long): ProjectEntity? = dao.getProjectById(id)

    suspend fun insert(project: ProjectEntity): Long = dao.insert(project)

    suspend fun update(project: ProjectEntity) = dao.update(project)

    suspend fun delete(project: ProjectEntity) = dao.delete(project)

    suspend fun deleteById(id: Long) = dao.deleteById(id)
}
