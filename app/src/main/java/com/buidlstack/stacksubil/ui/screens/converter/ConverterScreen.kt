package com.buidlstack.stacksubil.ui.screens.converter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items as lazyItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buidlstack.stacksubil.ui.screens.measurements.quantiaTextFieldColors
import com.buidlstack.stacksubil.ui.theme.*

data class ConversionUnit(val name: String, val toBase: Double)

object Converters {
    val length = listOf(
        ConversionUnit("mm", 0.001),
        ConversionUnit("cm", 0.01),
        ConversionUnit("m", 1.0),
        ConversionUnit("km", 1000.0),
        ConversionUnit("inch", 0.0254),
        ConversionUnit("ft", 0.3048),
        ConversionUnit("yard", 0.9144),
        ConversionUnit("mile", 1609.344)
    )
    val area = listOf(
        ConversionUnit("mm²", 0.000001),
        ConversionUnit("cm²", 0.0001),
        ConversionUnit("m²", 1.0),
        ConversionUnit("km²", 1_000_000.0),
        ConversionUnit("inch²", 0.00064516),
        ConversionUnit("ft²", 0.092903),
        ConversionUnit("yard²", 0.836127),
        ConversionUnit("acre", 4046.856)
    )
    val volume = listOf(
        ConversionUnit("ml", 0.001),
        ConversionUnit("L", 1.0),
        ConversionUnit("m³", 1000.0),
        ConversionUnit("inch³", 0.016387),
        ConversionUnit("ft³", 28.3168),
        ConversionUnit("gallon (US)", 3.78541),
        ConversionUnit("fl oz (US)", 0.0295735)
    )
    val weight = listOf(
        ConversionUnit("mg", 0.000001),
        ConversionUnit("g", 0.001),
        ConversionUnit("kg", 1.0),
        ConversionUnit("ton", 1000.0),
        ConversionUnit("oz", 0.0283495),
        ConversionUnit("lb", 0.453592)
    )

    fun convert(value: Double, from: ConversionUnit, to: ConversionUnit): Double {
        val baseValue = value * from.toBase
        return baseValue / to.toBase
    }
}

data class ConverterCategory(
    val name: String,
    val units: List<ConversionUnit>,
    val accentColor: Color
)

val converterCategories = listOf(
    ConverterCategory("Length", Converters.length, AccentCyan),
    ConverterCategory("Area", Converters.area, AccentMint),
    ConverterCategory("Volume", Converters.volume, AccentPurple),
    ConverterCategory("Weight", Converters.weight, Color(0xFF60A5FA))
)

@Composable
fun ConverterScreen() {
    var selectedCategoryIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                "Unit Converter",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(12.dp))
            ScrollableTabRow(
                selectedTabIndex = selectedCategoryIndex,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onSurface,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedCategoryIndex]),
                        color = converterCategories[selectedCategoryIndex].accentColor
                    )
                }
            ) {
                converterCategories.forEachIndexed { index, category ->
                    Tab(
                        selected = selectedCategoryIndex == index,
                        onClick = { selectedCategoryIndex = index },
                        text = {
                            Text(
                                category.name,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (selectedCategoryIndex == index)
                                    category.accentColor
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
        }

        val category = converterCategories[selectedCategoryIndex]
        ConverterPanel(category = category)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterPanel(category: ConverterCategory) {
    var inputValue by remember(category) { mutableStateOf("") }
    var fromUnitIndex by remember(category) { mutableStateOf(0) }
    var toUnitIndex by remember(category) { mutableStateOf(1) }
    var fromDropdownExpanded by remember { mutableStateOf(false) }
    var toDropdownExpanded by remember { mutableStateOf(false) }

    val fromUnit = category.units[fromUnitIndex]
    val toUnit = category.units[toUnitIndex]
    val result = inputValue.toDoubleOrNull()?.let {
        Converters.convert(it, fromUnit, toUnit)
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, category.accentColor.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Convert",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputValue,
                        onValueChange = { inputValue = it },
                        label = { Text("Value") },
                        placeholder = { Text("0") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp),
                        colors = quantiaTextFieldColors()
                    )

                    ExposedDropdownMenuBox(
                        expanded = fromDropdownExpanded,
                        onExpandedChange = { fromDropdownExpanded = it },
                        modifier = Modifier.weight(0.8f)
                    ) {
                        OutlinedTextField(
                            value = fromUnit.name,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("From") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromDropdownExpanded) },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = quantiaTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = fromDropdownExpanded,
                            onDismissRequest = { fromDropdownExpanded = false }
                        ) {
                            category.units.forEachIndexed { index, unit ->
                                DropdownMenuItem(
                                    text = { Text(unit.name) },
                                    onClick = { fromUnitIndex = index; fromDropdownExpanded = false }
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            val tmp = fromUnitIndex
                            fromUnitIndex = toUnitIndex
                            toUnitIndex = tmp
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(category.accentColor.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            Icons.Filled.SwapVert,
                            contentDescription = "Swap",
                            tint = category.accentColor
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(category.accentColor.copy(alpha = 0.1f))
                            .border(1.dp, category.accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 18.dp)
                    ) {
                        Text(
                            text = result?.let { formatConverterResult(it) } ?: "—",
                            style = MaterialTheme.typography.headlineSmall,
                            color = category.accentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    ExposedDropdownMenuBox(
                        expanded = toDropdownExpanded,
                        onExpandedChange = { toDropdownExpanded = it },
                        modifier = Modifier.weight(0.8f)
                    ) {
                        OutlinedTextField(
                            value = toUnit.name,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("To") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toDropdownExpanded) },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = quantiaTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = toDropdownExpanded,
                            onDismissRequest = { toDropdownExpanded = false }
                        ) {
                            category.units.forEachIndexed { index, unit ->
                                DropdownMenuItem(
                                    text = { Text(unit.name) },
                                    onClick = { toUnitIndex = index; toDropdownExpanded = false }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                "Common Conversions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        val commonConversions = getCommonConversions(category)
        lazyItems(commonConversions) { pair ->
            val fromU = pair.first
            val toU = pair.second
            CommonConversionRow(
                fromUnit = fromU,
                toUnit = toU,
                accentColor = category.accentColor,
                onClick = {
                    fromUnitIndex = category.units.indexOfFirst { it.name == fromU.name }.takeIf { it >= 0 } ?: 0
                    toUnitIndex = category.units.indexOfFirst { it.name == toU.name }.takeIf { it >= 0 } ?: 1
                }
            )
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
fun CommonConversionRow(
    fromUnit: ConversionUnit,
    toUnit: ConversionUnit,
    accentColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "1 ${fromUnit.name}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            Icon(
                Icons.Outlined.ArrowForward,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.padding(horizontal = 10.dp).size(18.dp)
            )
            Text(
                formatConverterResult(Converters.convert(1.0, fromUnit, toUnit)) + " ${toUnit.name}",
                style = MaterialTheme.typography.titleMedium,
                color = accentColor,
                fontWeight = FontWeight.SemiBold
            )
        }
        Icon(
            Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}

fun getCommonConversions(category: ConverterCategory): List<Pair<ConversionUnit, ConversionUnit>> {
    val units = category.units
    fun find(name: String) = units.find { it.name == name }

    return when (category.name) {
        "Length" -> listOf(
            find("cm") to find("inch"),
            find("m") to find("ft"),
            find("km") to find("mile"),
            find("inch") to find("cm"),
            find("ft") to find("m"),
            find("yard") to find("m"),
            find("m") to find("yard"),
            find("mile") to find("km")
        )
        "Area" -> listOf(
            find("m²") to find("ft²"),
            find("ft²") to find("m²"),
            find("m²") to find("yard²"),
            find("km²") to find("acre"),
            find("acre") to find("m²"),
            find("cm²") to find("inch²")
        )
        "Volume" -> listOf(
            find("L") to find("gallon (US)"),
            find("gallon (US)") to find("L"),
            find("m³") to find("ft³"),
            find("ft³") to find("m³"),
            find("ml") to find("fl oz (US)")
        )
        "Weight" -> listOf(
            find("kg") to find("lb"),
            find("lb") to find("kg"),
            find("g") to find("oz"),
            find("oz") to find("g"),
            find("ton") to find("kg"),
            find("kg") to find("ton")
        )
        else -> emptyList()
    }.mapNotNull { (a, b) -> if (a != null && b != null) a to b else null }
}

fun formatConverterResult(value: Double): String {
    if (value == 0.0) return "0"
    return when {
        value >= 1000 -> "%.2f".format(value).trimEnd('0').trimEnd('.')
        value >= 1 -> "%.4f".format(value).trimEnd('0').trimEnd('.')
        value >= 0.001 -> "%.6f".format(value).trimEnd('0').trimEnd('.')
        else -> "%.8f".format(value).trimEnd('0').trimEnd('.')
    }
}
