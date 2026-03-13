package com.buidlstack.stacksubil.ui.screens.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.buidlstack.stacksubil.data.db.entity.ProjectEntity
import com.buidlstack.stacksubil.ui.components.GradientButton
import com.buidlstack.stacksubil.ui.screens.measurements.quantiaTextFieldColors
import com.buidlstack.stacksubil.ui.theme.AccentCyan
import com.buidlstack.stacksubil.ui.theme.ProjectColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProjectScreen(
    viewModel: ProjectsViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    editProject: ProjectEntity? = null
) {
    var name by remember { mutableStateOf(editProject?.name ?: "") }
    var description by remember { mutableStateOf(editProject?.description ?: "") }
    var selectedColor by remember { mutableStateOf(editProject?.color ?: "#22D3EE") }
    var nameError by remember { mutableStateOf(false) }

    val colorHexList = listOf(
        "#22D3EE", "#2DD4BF", "#A78BFA", "#60A5FA",
        "#34D399", "#FACC15", "#FB923C", "#F472B6"
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (editProject != null) "Edit Project" else "New Project",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = false },
                label = { Text("Project Name *") },
                placeholder = { Text("e.g. Kitchen renovation") },
                isError = nameError,
                supportingText = if (nameError) ({ Text("Name is required") }) else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = quantiaTextFieldColors()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                placeholder = { Text("Optional description...") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = RoundedCornerShape(14.dp),
                colors = quantiaTextFieldColors()
            )

            Column {
                Text(
                    "Project Color",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(colorHexList) { hex ->
                        val color = try {
                            Color(android.graphics.Color.parseColor(hex))
                        } catch (e: Exception) { AccentCyan }
                        val isSelected = selectedColor == hex
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    if (isSelected) 3.dp else 0.dp,
                                    Color.White,
                                    CircleShape
                                )
                                .clickable { selectedColor = hex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            val previewColor = try {
                Color(android.graphics.Color.parseColor(selectedColor))
            } catch (e: Exception) { AccentCyan }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(previewColor.copy(alpha = 0.12f))
                    .border(1.dp, previewColor.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(previewColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Folder,
                            contentDescription = null,
                            tint = previewColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = name.ifEmpty { "Project name" },
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (description.isNotEmpty()) {
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            GradientButton(
                text = if (editProject != null) "Save Changes" else "Create Project",
                onClick = {
                    if (name.trim().isEmpty()) { nameError = true; return@GradientButton }
                    val project = (editProject ?: ProjectEntity(
                        name = name.trim(),
                        description = description,
                        color = selectedColor
                    )).copy(
                        name = name.trim(),
                        description = description,
                        color = selectedColor
                    )
                    if (editProject != null) {
                        viewModel.updateProject(project)
                    } else {
                        viewModel.addProject(project)
                    }
                    onSaved()
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}
