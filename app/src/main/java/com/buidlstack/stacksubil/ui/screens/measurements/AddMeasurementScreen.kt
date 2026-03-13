package com.buidlstack.stacksubil.ui.screens.measurements

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import com.buidlstack.stacksubil.ui.screens.dashboard.formatValue
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.buidlstack.stacksubil.data.db.entity.MeasurementEntity
import com.buidlstack.stacksubil.ui.components.GradientButton
import com.buidlstack.stacksubil.ui.screens.projects.ProjectsViewModel
import com.buidlstack.stacksubil.ui.theme.AccentCyan
import com.buidlstack.stacksubil.ui.theme.AccentMint

val MEASUREMENT_UNITS = listOf("mm", "cm", "m", "km", "inch", "ft", "yard", "mile")
val MEASUREMENT_CATEGORIES = listOf("Room", "Furniture", "Door/Window", "Floor", "Wall", "Equipment", "Other")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMeasurementScreen(
    viewModel: MeasurementsViewModel,
    projectsViewModel: ProjectsViewModel,
    prefillValue: String? = null,
    prefillUnit: String? = null,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    editMeasurement: MeasurementEntity? = null
) {
    val projects by viewModel.projects.collectAsState()
    val context = LocalContext.current

    var title by remember { mutableStateOf(editMeasurement?.title ?: "") }
    var valueText by remember { mutableStateOf(editMeasurement?.value?.let { formatValue(it) } ?: prefillValue ?: "") }
    var selectedUnit by remember { mutableStateOf(editMeasurement?.unit ?: prefillUnit ?: "cm") }
    var selectedProjectId by remember { mutableStateOf<Long?>(editMeasurement?.projectId) }
    var selectedCategory by remember { mutableStateOf(editMeasurement?.category ?: "") }
    var note by remember { mutableStateOf(editMeasurement?.note ?: "") }
    var photoUri by remember { mutableStateOf(editMeasurement?.photoUri ?: "") }

    var unitDropdownExpanded by remember { mutableStateOf(false) }
    var projectDropdownExpanded by remember { mutableStateOf(false) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    var titleError by remember { mutableStateOf(false) }
    var valueError by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}
            photoUri = it.toString()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (editMeasurement != null) "Edit Measurement" else "New Measurement",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it; titleError = false },
                label = { Text("Title *") },
                placeholder = { Text("e.g. Kitchen wall width") },
                isError = titleError,
                supportingText = if (titleError) ({ Text("Title is required") }) else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = quantiaTextFieldColors()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = valueText,
                    onValueChange = { valueText = it; valueError = false },
                    label = { Text("Value *") },
                    placeholder = { Text("0.0") },
                    isError = valueError,
                    supportingText = if (valueError) ({ Text("Enter a valid number") }) else null,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(14.dp),
                    colors = quantiaTextFieldColors()
                )

                ExposedDropdownMenuBox(
                    expanded = unitDropdownExpanded,
                    onExpandedChange = { unitDropdownExpanded = it },
                    modifier = Modifier.weight(0.7f)
                ) {
                    OutlinedTextField(
                        value = selectedUnit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitDropdownExpanded) },
                        modifier = Modifier.menuAnchor(),
                        shape = RoundedCornerShape(14.dp),
                        colors = quantiaTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = unitDropdownExpanded,
                        onDismissRequest = { unitDropdownExpanded = false }
                    ) {
                        MEASUREMENT_UNITS.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(unit) },
                                onClick = { selectedUnit = unit; unitDropdownExpanded = false }
                            )
                        }
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = projectDropdownExpanded,
                onExpandedChange = { projectDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = projects.find { it.id == selectedProjectId }?.name ?: "No project",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Project") },
                    leadingIcon = { Icon(Icons.Outlined.Folder, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = projectDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(14.dp),
                    colors = quantiaTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = projectDropdownExpanded,
                    onDismissRequest = { projectDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("No project") },
                        onClick = { selectedProjectId = null; projectDropdownExpanded = false }
                    )
                    projects.forEach { project ->
                        DropdownMenuItem(
                            text = { Text(project.name) },
                            onClick = { selectedProjectId = project.id; projectDropdownExpanded = false }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = categoryDropdownExpanded,
                onExpandedChange = { categoryDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedCategory.ifEmpty { "Select category" },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    leadingIcon = { Icon(Icons.Outlined.Category, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(14.dp),
                    colors = quantiaTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("None") },
                        onClick = { selectedCategory = ""; categoryDropdownExpanded = false }
                    )
                    MEASUREMENT_CATEGORIES.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = { selectedCategory = cat; categoryDropdownExpanded = false }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note") },
                placeholder = { Text("Optional notes...") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = RoundedCornerShape(14.dp),
                colors = quantiaTextFieldColors()
            )

            Column {
                Text(
                    "Photo",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                if (photoUri.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(14.dp))
                    ) {
                        AsyncImage(
                            model = Uri.parse(photoUri),
                            contentDescription = "Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { photoUri = "" },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Remove photo",
                                tint = Color.White
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { photoPickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Outlined.AddPhotoAlternate,
                                contentDescription = "Add photo",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Tap to add photo",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            GradientButton(
                text = if (editMeasurement != null) "Save Changes" else "Save Measurement",
                onClick = {
                    val trimmedTitle = title.trim()
                    val value = valueText.toDoubleOrNull()
                    if (trimmedTitle.isEmpty()) { titleError = true; return@GradientButton }
                    if (value == null) { valueError = true; return@GradientButton }

                    val measurement = (editMeasurement ?: MeasurementEntity(
                        title = trimmedTitle,
                        value = value,
                        unit = selectedUnit
                    )).copy(
                        title = trimmedTitle,
                        value = value,
                        unit = selectedUnit,
                        projectId = selectedProjectId,
                        category = selectedCategory,
                        note = note,
                        photoUri = photoUri
                    )
                    if (editMeasurement != null) {
                        viewModel.updateMeasurement(measurement)
                    } else {
                        viewModel.addMeasurement(measurement)
                    }
                    onSaved()
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun quantiaTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AccentCyan,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    focusedLabelColor = AccentCyan
)
