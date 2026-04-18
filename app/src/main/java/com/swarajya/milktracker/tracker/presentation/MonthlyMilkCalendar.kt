package com.swarajya.milktracker.tracker.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swarajya.milktracker.R
import com.swarajya.milktracker.common.presentation.theme.*
import com.swarajya.milktracker.tracker.presentation.viewmodel.TrackerViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthlyMilkCalendar(
    viewModel: TrackerViewModel = hiltViewModel()
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    val currentMonth by viewModel.currentMonth.collectAsStateWithLifecycle()
    val milkLogs by viewModel.milkLogs.collectAsStateWithLifecycle()
    val showBottomSheet by viewModel.showBottomSheet.collectAsStateWithLifecycle()
    val defaultPrice by viewModel.defaultPrice.collectAsStateWithLifecycle()
    val today = LocalDate.now()

    val snackbarHostState = remember { SnackbarHostState() }
    
    val saveSuccessMessage = stringResource(id = R.string.save_success)
    val saveErrorMessage = stringResource(id = R.string.save_error)

    // Calculate Monthly Totals
    val totalQuantity = milkLogs.values.sumOf { (it.morningQty + it.eveningQty).toDouble() }.toFloat()
    val totalAmount = milkLogs.values.sumOf { ((it.morningQty + it.eveningQty) * it.pricePerLiter).toDouble() }.toFloat()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is TrackerViewModel.UiEvent.Success -> {
                    snackbarHostState.showSnackbar(
                        message = saveSuccessMessage
                    )
                }
                is TrackerViewModel.UiEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        message = saveErrorMessage
                    )
                }
            }
        }
    }

    val milkyGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.surface
        )
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(milkyGradient)
                .padding(paddingValues)
                .padding(DIMENSIONS_16DP)
        ) {
            // Month and Year Header with Navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { viewModel.onMonthChange(currentMonth.minusMonths(1)) }) {
                    Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "Previous Month")
                }
                
                Text(
                    text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = { viewModel.onMonthChange(currentMonth.plusMonths(1)) }) {
                    Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Next Month")
                }
            }

            Spacer(modifier = Modifier.height(DIMENSIONS_16DP))

            // Days of the Week Header
            Row(modifier = Modifier.fillMaxWidth()) {
                val daysOfWeek = listOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY)
                daysOfWeek.forEach { day ->
                    Text(
                        text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()).substring(0, 1),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(DIMENSIONS_8DP))

            // Calendar Grid
            val daysInMonth = currentMonth.lengthOfMonth()
            val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value % 7 

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(firstDayOfMonth) {
                    Box(modifier = Modifier.aspectRatio(1f))
                }

                items(daysInMonth) { dayIndex ->
                    val date = currentMonth.atDay(dayIndex + 1)
                    val isSelected = date == selectedDate
                    
                    // Only show dot if log exists AND quantity > 0
                    val log = milkLogs[date]
                    val hasLog = log != null && (log.morningQty > 0f || log.eveningQty > 0f)
                    
                    val isFutureDate = date.isAfter(today)

                    CalendarDayCell(
                        date = date,
                        isSelected = isSelected,
                        hasLog = hasLog,
                        enabled = !isFutureDate,
                        onClick = { 
                            selectedDate = date
                            viewModel.onToggleBottomSheet(true)
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(DIMENSIONS_24DP))

            // Monthly Summary Section
            MonthlySummaryCard(
                totalQuantity = totalQuantity,
                totalAmount = totalAmount
            )

            if (showBottomSheet) {
                val existingLog = milkLogs[selectedDate]
                AddMilkBottomSheet(
                    selectedDate = selectedDate.toString(),
                    onDismiss = { viewModel.onToggleBottomSheet(false) },
                    initialMorningQty = existingLog?.morningQty ?: 0.0f,
                    initialEveningQty = existingLog?.eveningQty ?: 0.0f,
                    initialPrice = existingLog?.pricePerLiter ?: defaultPrice,
                    onSave = { morning, evening, price ->
                        viewModel.saveMilkLog(
                            date = selectedDate.toString(),
                            morningQty = morning,
                            eveningQty = evening,
                            price = price
                        )
                    },
                    onDelete = {
                        viewModel.deleteMilkLog(selectedDate.toString())
                    }
                )
            }
        }
    }
}

@Composable
fun MonthlySummaryCard(
    totalQuantity: Float,
    totalAmount: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(DIMENSIONS_16DP),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(DIMENSIONS_16DP)
        ) {
            Text(
                text = stringResource(id = R.string.monthly_summary),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Spacer(modifier = Modifier.height(DIMENSIONS_8DP))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.total_quantity, totalQuantity),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(id = R.string.total_amount, totalAmount),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarDayCell(
    date: LocalDate,
    isSelected: Boolean,
    hasLog: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(DIMENSIONS_8DP)
    
    val modifier = Modifier
        .padding(DIMENSIONS_4DP)
        .aspectRatio(1f)
        .clip(shape)
        .then(
            when {
                isSelected && enabled -> Modifier.border(2.dp, MaterialTheme.colorScheme.primary, shape)
                enabled -> Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
                else -> Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.38f), shape)
            }
        )
        .background(
            color = when {
                isSelected && enabled -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                hasLog && enabled -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                enabled -> MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                else -> Color.Transparent
            }
        )
        .then(if (enabled) Modifier.clickable { onClick() } else Modifier)

    val textColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        isSelected -> MaterialTheme.colorScheme.primary
        hasLog -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = date.dayOfMonth.toString(),
                color = textColor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected || hasLog) FontWeight.Bold else FontWeight.Normal
            )
            
            if (hasLog) {
                Spacer(modifier = Modifier.height(DIMENSIONS_2DP))
                Box(
                    modifier = Modifier
                        .size(DIMENSIONS_6DP)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.tertiary
                        )
                )
            }
        }
    }
}
