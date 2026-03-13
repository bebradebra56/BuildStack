package com.buidlstack.stacksubil.ui.screens.history

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.buidlstack.stacksubil.data.db.entity.HistoryEntity
import com.buidlstack.stacksubil.ui.components.EmptyState
import com.buidlstack.stacksubil.ui.theme.AccentCyan
import com.buidlstack.stacksubil.ui.theme.AccentMint
import com.buidlstack.stacksubil.ui.theme.AccentPurple
import com.buidlstack.stacksubil.ui.theme.ErrorRed
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = viewModel()
) {
    val history by viewModel.history.collectAsStateWithLifecycle()
    var showClearDialog by remember { mutableStateOf(false) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear History") },
            text = { Text("Are you sure you want to clear all history?") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.clearHistory(); showClearDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = ErrorRed)
                ) { Text("Clear") }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (history.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(Icons.Outlined.DeleteSweep, contentDescription = "Clear history", tint = ErrorRed.copy(alpha = 0.7f))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        if (history.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                EmptyState(
                    icon = Icons.Outlined.History,
                    title = "No history",
                    subtitle = "Actions will appear here"
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(padding)
            ) {
                items(history, key = { it.id }) { item ->
                    HistoryItem(historyEntity = item)
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun HistoryItem(historyEntity: HistoryEntity) {
    val sdf = remember { SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault()) }
    val (icon, color) = getHistoryIconAndColor(historyEntity.action)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                historyEntity.action,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (historyEntity.entityTitle.isNotEmpty()) {
                Text(
                    historyEntity.entityTitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = color,
                    maxLines = 1
                )
            }
            Text(
                sdf.format(Date(historyEntity.timestamp)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

fun getHistoryIconAndColor(action: String): Pair<ImageVector, Color> {
    return when {
        action.contains("Added", ignoreCase = true) -> Icons.Outlined.Add to AccentCyan
        action.contains("Updated", ignoreCase = true) -> Icons.Outlined.Edit to AccentMint
        action.contains("Deleted", ignoreCase = true) -> Icons.Outlined.Delete to ErrorRed
        action.contains("Duplicated", ignoreCase = true) -> Icons.Outlined.ContentCopy to AccentPurple
        action.contains("Created", ignoreCase = true) -> Icons.Outlined.CreateNewFolder to AccentMint
        else -> Icons.Outlined.History to AccentCyan
    }
}
