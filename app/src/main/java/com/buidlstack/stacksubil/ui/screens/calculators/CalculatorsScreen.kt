package com.buidlstack.stacksubil.ui.screens.calculators

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buidlstack.stacksubil.ui.components.SectionHeader
import com.buidlstack.stacksubil.ui.screens.measurements.quantiaTextFieldColors
import com.buidlstack.stacksubil.ui.theme.*
import kotlin.math.ceil

@Composable
fun CalculatorsScreen(
    onSaveToMeasurements: (String, String) -> Unit = { _, _ -> }
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "Calculators",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        AreaCalculator(onSave = { result -> onSaveToMeasurements(result, "m²") })
        VolumeCalculator(onSave = { result -> onSaveToMeasurements(result, "m³") })
        PaintCalculator(onSave = { result -> onSaveToMeasurements(result, "L") })
        TileCalculator(onSave = { result -> onSaveToMeasurements(result, "tiles") })

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun AreaCalculator(onSave: (String) -> Unit) {
    var length by remember { mutableStateOf("") }
    var width by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<Double?>(null) }

    CalculatorCard(
        title = "Area Calculator",
        icon = Icons.Outlined.CropSquare,
        accentColor = AccentCyan,
        resultLabel = "Area",
        resultValue = result?.let { "${formatCalcResult(it)} m²" },
        onSave = result?.let { { onSave(formatCalcResult(it)) } }
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CalculatorField(
                value = length,
                onValueChange = { length = it; result = null },
                label = "Length (m)",
                modifier = Modifier.weight(1f)
            )
            CalculatorField(
                value = width,
                onValueChange = { width = it; result = null },
                label = "Width (m)",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = {
                val l = length.toDoubleOrNull()
                val w = width.toDoubleOrNull()
                if (l != null && w != null) result = l * w
            },
            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Calculate", fontWeight = FontWeight.Bold) }
    }
}

@Composable
fun VolumeCalculator(onSave: (String) -> Unit) {
    var length by remember { mutableStateOf("") }
    var width by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<Double?>(null) }

    CalculatorCard(
        title = "Volume Calculator",
        icon = Icons.Outlined.ViewInAr,
        accentColor = AccentMint,
        resultLabel = "Volume",
        resultValue = result?.let { "${formatCalcResult(it)} m³" },
        onSave = result?.let { { onSave(formatCalcResult(it)) } }
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CalculatorField(value = length, onValueChange = { length = it; result = null }, label = "Length (m)", modifier = Modifier.weight(1f))
            CalculatorField(value = width, onValueChange = { width = it; result = null }, label = "Width (m)", modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(10.dp))
        CalculatorField(value = height, onValueChange = { height = it; result = null }, label = "Height (m)", modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = {
                val l = length.toDoubleOrNull()
                val w = width.toDoubleOrNull()
                val h = height.toDoubleOrNull()
                if (l != null && w != null && h != null) result = l * w * h
            },
            colors = ButtonDefaults.buttonColors(containerColor = AccentMint, contentColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Calculate", fontWeight = FontWeight.Bold) }
    }
}

@Composable
fun PaintCalculator(onSave: (String) -> Unit) {
    var wallWidth by remember { mutableStateOf("") }
    var wallHeight by remember { mutableStateOf("") }
    var coverage by remember { mutableStateOf("10") }
    var coats by remember { mutableStateOf("2") }
    var result by remember { mutableStateOf<Double?>(null) }

    CalculatorCard(
        title = "Paint Calculator",
        icon = Icons.Outlined.ColorLens,
        accentColor = AccentPurple,
        resultLabel = "Paint needed",
        resultValue = result?.let { "${formatCalcResult(it)} liters" },
        onSave = result?.let { { onSave(formatCalcResult(it)) } }
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CalculatorField(value = wallWidth, onValueChange = { wallWidth = it; result = null }, label = "Wall width (m)", modifier = Modifier.weight(1f))
            CalculatorField(value = wallHeight, onValueChange = { wallHeight = it; result = null }, label = "Wall height (m)", modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CalculatorField(value = coverage, onValueChange = { coverage = it; result = null }, label = "Coverage (m²/L)", modifier = Modifier.weight(1f))
            CalculatorField(value = coats, onValueChange = { coats = it; result = null }, label = "Coats", modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = {
                val w = wallWidth.toDoubleOrNull()
                val h = wallHeight.toDoubleOrNull()
                val c = coverage.toDoubleOrNull()
                val ct = coats.toDoubleOrNull() ?: 2.0
                if (w != null && h != null && c != null && c > 0) {
                    result = (w * h * ct) / c
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple, contentColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Calculate", fontWeight = FontWeight.Bold) }
    }
}

@Composable
fun TileCalculator(onSave: (String) -> Unit) {
    var roomWidth by remember { mutableStateOf("") }
    var roomHeight by remember { mutableStateOf("") }
    var tileSize by remember { mutableStateOf("") }
    var waste by remember { mutableStateOf("10") }
    var result by remember { mutableStateOf<Int?>(null) }

    CalculatorCard(
        title = "Tile Calculator",
        icon = Icons.Outlined.GridOn,
        accentColor = Color(0xFF60A5FA),
        resultLabel = "Tiles needed",
        resultValue = result?.let { "$it tiles" },
        onSave = result?.let { { onSave(it.toString()) } }
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CalculatorField(value = roomWidth, onValueChange = { roomWidth = it; result = null }, label = "Room width (m)", modifier = Modifier.weight(1f))
            CalculatorField(value = roomHeight, onValueChange = { roomHeight = it; result = null }, label = "Room height (m)", modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CalculatorField(value = tileSize, onValueChange = { tileSize = it; result = null }, label = "Tile size (cm)", modifier = Modifier.weight(1f))
            CalculatorField(value = waste, onValueChange = { waste = it; result = null }, label = "Waste (%)", modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = {
                val rw = roomWidth.toDoubleOrNull()
                val rh = roomHeight.toDoubleOrNull()
                val ts = tileSize.toDoubleOrNull()
                val w = waste.toDoubleOrNull() ?: 10.0
                if (rw != null && rh != null && ts != null && ts > 0) {
                    val tileSizeM = ts / 100.0
                    val tilesNeeded = (rw * rh) / (tileSizeM * tileSizeM)
                    result = ceil(tilesNeeded * (1 + w / 100)).toInt()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF60A5FA), contentColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Calculate", fontWeight = FontWeight.Bold) }
    }
}

@Composable
fun CalculatorCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    resultLabel: String,
    resultValue: String?,
    onSave: (() -> Unit)?,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(10.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(Modifier.height(16.dp))
        content()

        if (resultValue != null) {
            Spacer(Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.1f))
                    .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(resultLabel, style = MaterialTheme.typography.labelSmall, color = accentColor.copy(alpha = 0.8f))
                        Text(
                            resultValue,
                            style = MaterialTheme.typography.headlineMedium,
                            color = accentColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }
                    if (onSave != null) {
                        TextButton(
                            onClick = onSave,
                            colors = ButtonDefaults.textButtonColors(contentColor = accentColor)
                        ) {
                            Icon(Icons.Outlined.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Save", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        shape = RoundedCornerShape(12.dp),
        colors = quantiaTextFieldColors()
    )
}

fun formatCalcResult(value: Double): String {
    return if (value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        "%.4f".format(value).trimEnd('0').trimEnd('.')
    }
}
