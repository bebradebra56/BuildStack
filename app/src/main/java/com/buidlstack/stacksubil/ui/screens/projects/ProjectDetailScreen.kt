package com.buidlstack.stacksubil.ui.screens.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.buidlstack.stacksubil.data.db.entity.ProjectEntity
import com.buidlstack.stacksubil.ui.components.EmptyState
import com.buidlstack.stacksubil.ui.screens.measurements.MeasurementListItem
import com.buidlstack.stacksubil.ui.screens.measurements.MeasurementsViewModel
import com.buidlstack.stacksubil.ui.theme.AccentCyan
import com.buidlstack.stacksubil.ui.theme.ErrorRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: Long,
    viewModel: ProjectsViewModel,
    measurementsViewModel: MeasurementsViewModel,
    onBack: () -> Unit,
    onNavigateToMeasurement: (Long) -> Unit,
    onAddMeasurement: () -> Unit
) {
    var project by remember { mutableStateOf<ProjectEntity?>(null) }
    var showEditScreen by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(projectId) {
        project = viewModel.getProjectById(projectId)
    }

    val measurements by remember(projectId) {
        viewModel.getMeasurementsForProject(projectId)
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    if (showEditScreen && project != null) {
        AddProjectScreen(
            viewModel = viewModel,
            editProject = project,
            onBack = { showEditScreen = false },
            onSaved = {
                showEditScreen = false
            }
        )
        return
    }

    val p = project ?: run {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AccentCyan)
        }
        return
    }

    val color = try { Color(android.graphics.Color.parseColor(p.color)) } catch (e: Exception) { AccentCyan }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Project") },
            text = { Text("Delete '${p.name}'? Measurements won't be deleted.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteProject(p); showDeleteDialog = false; onBack() },
                    colors = ButtonDefaults.textButtonColors(contentColor = ErrorRed)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(p.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditScreen = true }) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = AccentCyan)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = ErrorRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddMeasurement,
                containerColor = color,
                contentColor = Color(0xFF0F172A),
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Measurement")
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(padding)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(color.copy(alpha = 0.1f))
                        .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Folder, contentDescription = null, tint = color, modifier = Modifier.size(30.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(p.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        if (p.description.isNotEmpty()) {
                            Text(p.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(color.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("${measurements.size} measurements", style = MaterialTheme.typography.labelSmall, color = color)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Measurements", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(8.dp))
            }

            if (measurements.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Outlined.Straighten,
                        title = "No measurements",
                        subtitle = "Add measurements to this project"
                    )
                }
            } else {
                items(measurements, key = { it.id }) { m ->
                    MeasurementListItem(
                        measurement = m,
                        projectName = null,
                        onClick = { onNavigateToMeasurement(m.id) },
                        onFavoriteToggle = { measurementsViewModel.toggleFavorite(m) }
                    )
                }
            }
            item { Spacer(Modifier.height(72.dp)) }
        }
    }
}
