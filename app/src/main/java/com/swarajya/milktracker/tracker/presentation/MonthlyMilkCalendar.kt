package com.swarajya.milktracker.tracker.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swarajya.milktracker.common.presentation.theme.*
import com.swarajya.milktracker.tracker.presentation.viewmodel.TrackerViewModel
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

    Column(modifier = Modifier.fillMaxWidth().padding(DIMENSIONS_16DP)) {
        // Month and Year Header
        Text(
            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = DIMENSIONS_16DP)
        )

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
                val hasLog = milkLogs.containsKey(date)

                CalendarDayCell(
                    date = date,
                    isSelected = isSelected,
                    hasLog = hasLog,
                    onClick = { 
                        selectedDate = date
                        viewModel.onToggleBottomSheet(true)
                    }
                )
            }
        }
        
        if (showBottomSheet) {
            val existingLog = milkLogs[selectedDate]
            AddMilkBottomSheet(
                selectedDate = selectedDate.toString(),
                onDismiss = { viewModel.onToggleBottomSheet(false) },
                initialMorningQty = existingLog?.morningQty ?: 1.0f,
                initialEveningQty = existingLog?.eveningQty ?: 0.0f,
                initialPrice = existingLog?.pricePerLiter ?: 60.0f,
                onSave = { morning, evening, price ->
                    viewModel.saveMilkLog(
                        date = selectedDate.toString(),
                        morningQty = morning,
                        eveningQty = evening,
                        price = price
                    )
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarDayCell(
    date: LocalDate,
    isSelected: Boolean,
    hasLog: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .padding(DIMENSIONS_4DP)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = date.dayOfMonth.toString(),
                color = textColor,
                style = MaterialTheme.typography.bodyMedium
            )
            
            if (hasLog) {
                Spacer(modifier = Modifier.height(DIMENSIONS_2DP))
                Box(
                    modifier = Modifier
                        .size(DIMENSIONS_4DP)
                        .clip(CircleShape)
                        .background(if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.tertiary)
                )
            }
        }
    }
}
