package com.buidlstack.stacksubil.ui.screens.measurements

import android.net.Uri
import androidx.compose.foundation.background
import com.buidlstack.stacksubil.ui.screens.dashboard.formatValue
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.buidlstack.stacksubil.data.db.entity.MeasurementEntity
import com.buidlstack.stacksubil.ui.components.UnitBadge
import com.buidlstack.stacksubil.ui.screens.projects.ProjectsViewModel
import com.buidlstack.stacksubil.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementDetailScreen(
    measurementId: Long,
    viewModel: MeasurementsViewModel,
    projectsViewModel: ProjectsViewModel,
    onBack: () -> Unit,
    onAddToCalculator: (String, String) -> Unit
) {
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    var measurement by remember { mutableStateOf<MeasurementEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditScreen by remember { mutableStateOf(false) }

    LaunchedEffect(measurementId) {
        measurement = viewModel.getMeasurementById(measurementId)
    }

    if (showEditScreen && measurement != null) {
        AddMeasurementScreen(
            viewModel = viewModel,
            projectsViewModel = projectsViewModel,
            editMeasurement = measurement,
            onBack = { showEditScreen = false },
            onSaved = {
                showEditScreen = false
            }
        )
        return
    }

    val m = measurement ?: run {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AccentCyan)
        }
        return
    }

    val sdf = remember { SimpleDateFormat("MMMM d, yyyy 'at' HH:mm", Locale.getDefault()) }
    val projectName = projects.find { it.id == m.projectId }?.name

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Measurement") },
            text = { Text("Are you sure you want to delete '${m.title}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMeasurement(m)
                        showDeleteDialog = false
                        onBack()
                    },
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
                title = { Text(m.title, fontWeight = FontWeight.Bold, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite(m); measurement = m.copy(isFavorite = !m.isFavorite) }) {
                        Icon(
                            if (m.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = "Favorite",
                            tint = if (m.isFavorite) Color(0xFFFACC15) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = ErrorRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                AccentCyan.copy(alpha = 0.2f),
                                AccentMint.copy(alpha = 0.1f)
                            )
                        )
                    )
                    .border(1.dp, AccentCyan.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = formatValue(m.value),
                            style = MaterialTheme.typography.displayLarge,
                            color = AccentCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(8.dp))
                        Box(modifier = Modifier.padding(bottom = 8.dp)) {
                            UnitBadge(unit = m.unit)
                        }
                    }
                    Text(
                        text = m.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (m.photoUri.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    AsyncImage(
                        model = Uri.parse(m.photoUri),
                        contentDescription = "Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (projectName != null) {
                        DetailRow(icon = Icons.Filled.Folder, label = "Project", value = projectName)
                    }
                    if (m.category.isNotEmpty()) {
                        DetailRow(icon = Icons.Filled.Category, label = "Category", value = m.category)
                    }
                    DetailRow(icon = Icons.Outlined.Schedule, label = "Added", value = sdf.format(Date(m.date)))
                    if (m.note.isNotEmpty()) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                Icons.Outlined.Notes,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = m.note,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Text(
                "Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionButton(
                    icon = Icons.Outlined.Edit,
                    label = "Edit",
                    color = AccentCyan,
                    onClick = { showEditScreen = true },
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    icon = Icons.Outlined.ContentCopy,
                    label = "Duplicate",
                    color = AccentMint,
                    onClick = { viewModel.duplicateMeasurement(m); onBack() },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionButton(
                    icon = Icons.Outlined.Calculate,
                    label = "Calculator",
                    color = AccentPurple,
                    onClick = { onAddToCalculator(formatValue(m.value), m.unit) },
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    icon = Icons.Outlined.SwapHoriz,
                    label = "Convert",
                    color = Color(0xFF60A5FA),
                    onClick = { onAddToCalculator(formatValue(m.value), m.unit) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = color.copy(alpha = 0.12f),
            contentColor = color
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, fontWeight = FontWeight.Medium)
    }
}
