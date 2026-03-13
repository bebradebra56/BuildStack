package com.buidlstack.stacksubil.ui.screens.measurements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.buidlstack.stacksubil.data.db.entity.MeasurementEntity
import com.buidlstack.stacksubil.data.db.entity.ProjectEntity
import com.buidlstack.stacksubil.ui.components.EmptyState
import com.buidlstack.stacksubil.ui.components.FavoriteButton
import com.buidlstack.stacksubil.ui.components.QuantiaDivider
import com.buidlstack.stacksubil.ui.components.UnitBadge
import com.buidlstack.stacksubil.ui.screens.dashboard.formatValue
import com.buidlstack.stacksubil.ui.theme.AccentCyan
import com.buidlstack.stacksubil.ui.theme.AccentMint
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MeasurementsScreen(
    viewModel: MeasurementsViewModel,
    projectsViewModel: com.buidlstack.stacksubil.ui.screens.projects.ProjectsViewModel,
    onMeasurementClick: (Long) -> Unit,
    onAddMeasurement: () -> Unit
) {
    val measurements by viewModel.measurements.collectAsStateWithLifecycle()
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val projectFilter by viewModel.projectFilter.collectAsStateWithLifecycle()
    val unitFilter by viewModel.unitFilter.collectAsStateWithLifecycle()

    val allUnits = listOf("mm", "cm", "m", "km", "inch", "ft", "yard")

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddMeasurement,
                containerColor = AccentCyan,
                contentColor = Color(0xFF0F172A),
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Measurement")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    "Measurements",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(12.dp))
                SearchBar(
                    query = searchQuery,
                    onQueryChange = viewModel::setSearchQuery
                )
                Spacer(Modifier.height(10.dp))
                FilterChipsRow(
                    projects = projects,
                    selectedProjectId = projectFilter,
                    onProjectSelected = viewModel::setProjectFilter,
                    selectedUnit = unitFilter,
                    onUnitSelected = viewModel::setUnitFilter,
                    units = allUnits
                )
            }

            QuantiaDivider()

            if (measurements.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        icon = Icons.Outlined.Straighten,
                        title = "No measurements",
                        subtitle = if (searchQuery.isNotEmpty() || projectFilter != null) {
                            "Try clearing filters"
                        } else {
                            "Tap + to add your first measurement"
                        }
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(measurements, key = { it.id }) { m ->
                        MeasurementListItem(
                            measurement = m,
                            projectName = projects.find { it.id == m.projectId }?.name,
                            onClick = { onMeasurementClick(m.id) },
                            onFavoriteToggle = { viewModel.toggleFavorite(m) }
                        )
                    }
                    item { Spacer(Modifier.height(72.dp)) }
                }
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                "Search measurements...",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        },
        leadingIcon = {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentCyan,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun FilterChipsRow(
    projects: List<ProjectEntity>,
    selectedProjectId: Long?,
    onProjectSelected: (Long?) -> Unit,
    selectedUnit: String?,
    onUnitSelected: (String?) -> Unit,
    units: List<String>
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            FilterChip(
                selected = selectedProjectId == null && selectedUnit == null,
                onClick = { onProjectSelected(null); onUnitSelected(null) },
                label = { Text("All") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AccentCyan.copy(alpha = 0.2f),
                    selectedLabelColor = AccentCyan
                )
            )
        }
        items(projects) { project ->
            FilterChip(
                selected = selectedProjectId == project.id,
                onClick = {
                    onProjectSelected(if (selectedProjectId == project.id) null else project.id)
                },
                label = { Text(project.name, maxLines = 1) },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Folder,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AccentMint.copy(alpha = 0.2f),
                    selectedLabelColor = AccentMint
                )
            )
        }
        items(units) { unit ->
            FilterChip(
                selected = selectedUnit == unit,
                onClick = { onUnitSelected(if (selectedUnit == unit) null else unit) },
                label = { Text(unit) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AccentCyan.copy(alpha = 0.15f),
                    selectedLabelColor = AccentCyan
                )
            )
        }
    }
}

@Composable
fun MeasurementListItem(
    measurement: MeasurementEntity,
    projectName: String?,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    val sdf = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                1.dp,
                if (measurement.isFavorite) Color(0xFFFACC15).copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(start = 14.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AccentCyan.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Straighten,
                contentDescription = null,
                tint = AccentCyan,
                modifier = Modifier.size(22.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = measurement.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Spacer(Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatValue(measurement.value),
                    style = MaterialTheme.typography.bodyLarge,
                    color = AccentCyan,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(6.dp))
                UnitBadge(unit = measurement.unit)
            }
            if (projectName != null || measurement.date > 0) {
                Spacer(Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (projectName != null) {
                        Icon(
                            Icons.Filled.Folder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = projectName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(
                        text = sdf.format(Date(measurement.date)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }

        FavoriteButton(
            isFavorite = measurement.isFavorite,
            onClick = onFavoriteToggle
        )
    }
}
