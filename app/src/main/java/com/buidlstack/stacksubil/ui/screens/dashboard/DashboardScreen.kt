package com.buidlstack.stacksubil.ui.screens.dashboard

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.buidlstack.stacksubil.data.db.entity.MeasurementEntity
import com.buidlstack.stacksubil.data.db.entity.ProjectEntity
import com.buidlstack.stacksubil.ui.components.EmptyState
import com.buidlstack.stacksubil.ui.components.QuantiaCard
import com.buidlstack.stacksubil.ui.components.SectionHeader
import com.buidlstack.stacksubil.ui.components.UnitBadge
import com.buidlstack.stacksubil.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    onNavigateToMeasurements: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToCalculators: () -> Unit,
    onNavigateToConverter: () -> Unit,
    onMeasurementClick: (Long) -> Unit,
    onProjectClick: (Long) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val recentMeasurements by viewModel.recentMeasurements.collectAsStateWithLifecycle()
    val recentProjects by viewModel.recentProjects.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            DashboardHeader(
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToSettings = onNavigateToSettings
            )
        }

        item {
            Spacer(Modifier.height(8.dp))
            SectionHeader(
                title = "Recent Measurements",
                modifier = Modifier.padding(horizontal = 16.dp),
                trailing = {
                    TextButton(onClick = onNavigateToMeasurements) {
                        Text("See all", color = AccentCyan, style = MaterialTheme.typography.labelMedium)
                    }
                }
            )
            Spacer(Modifier.height(12.dp))
        }

        item {
            if (recentMeasurements.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No measurements yet. Tap + to add one.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recentMeasurements) { m ->
                        RecentMeasurementCard(
                            measurement = m,
                            onClick = { onMeasurementClick(m.id) }
                        )
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(24.dp))
            SectionHeader(
                title = "Quick Calculators",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickCalcButton(
                    icon = Icons.Outlined.CropSquare,
                    label = "Area",
                    color = AccentCyan,
                    onClick = onNavigateToCalculators,
                    modifier = Modifier.weight(1f)
                )
                QuickCalcButton(
                    icon = Icons.Outlined.ViewInAr,
                    label = "Volume",
                    color = AccentMint,
                    onClick = onNavigateToCalculators,
                    modifier = Modifier.weight(1f)
                )
                QuickCalcButton(
                    icon = Icons.Outlined.ColorLens,
                    label = "Paint",
                    color = AccentPurple,
                    onClick = onNavigateToCalculators,
                    modifier = Modifier.weight(1f)
                )
                QuickCalcButton(
                    icon = Icons.Outlined.GridOn,
                    label = "Tiles",
                    color = Color(0xFF60A5FA),
                    onClick = onNavigateToCalculators,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Spacer(Modifier.height(24.dp))
            SectionHeader(
                title = "Projects",
                modifier = Modifier.padding(horizontal = 16.dp),
                trailing = {
                    TextButton(onClick = onNavigateToProjects) {
                        Text("See all", color = AccentCyan, style = MaterialTheme.typography.labelMedium)
                    }
                }
            )
            Spacer(Modifier.height(12.dp))
        }

        item {
            if (recentProjects.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No projects yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recentProjects.take(5)) { project ->
                        ProjectCard(
                            project = project,
                            onClick = { onProjectClick(project.id) }
                        )
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(24.dp))
            UnitConverterWidget(onClick = onNavigateToConverter)
        }
    }
}

@Composable
fun DashboardHeader(
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Build Stack",
                style = MaterialTheme.typography.headlineLarge.copy(
                    brush = Brush.horizontalGradient(listOf(AccentCyan, AccentMint))
                ),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Smart measurement notes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row {
            IconButton(onClick = onNavigateToHistory) {
                Icon(
                    Icons.Outlined.History,
                    contentDescription = "History",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onNavigateToSettings) {
                Icon(
                    Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun RecentMeasurementCard(
    measurement: MeasurementEntity,
    onClick: () -> Unit
) {
    val sdf = remember { SimpleDateFormat("MMM d", Locale.getDefault()) }
    Box(
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, AccentCyan.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AccentCyan.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Straighten,
                    contentDescription = null,
                    tint = AccentCyan,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = measurement.title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatValue(measurement.value),
                    style = MaterialTheme.typography.titleLarge,
                    color = AccentCyan,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(4.dp))
                UnitBadge(unit = measurement.unit)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = sdf.format(Date(measurement.date)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun QuickCalcButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )
    }
}

@Composable
fun ProjectCard(
    project: ProjectEntity,
    onClick: () -> Unit
) {
    val color = try {
        Color(android.graphics.Color.parseColor(project.color))
    } catch (e: Exception) { AccentCyan }

    Box(
        modifier = Modifier
            .width(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Folder,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = project.name,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            if (project.description.isNotEmpty()) {
                Text(
                    text = project.description,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun UnitConverterWidget(onClick: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader(title = "Unit Converter")
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, AccentMint.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .clickable(onClick = onClick)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Quick conversions",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        Icons.Outlined.SwapHoriz,
                        contentDescription = null,
                        tint = AccentMint
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("cm → m", "inch → cm", "m² → ft²").forEach { conv ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentMint.copy(alpha = 0.1f))
                                .border(1.dp, AccentMint.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = conv,
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentMint,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

fun formatValue(value: Double): String {
    return if (value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        val s = "%.4f".format(value).trimEnd('0')
        if (s.endsWith('.')) s.dropLast(1) else s
    }
}
