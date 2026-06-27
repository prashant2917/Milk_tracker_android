package com.swarajya.milktracker.tracker.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.swarajya.milktracker.R
import com.swarajya.milktracker.common.constants.AnalyticsConstants
import com.swarajya.milktracker.common.constants.AppConstants
import com.swarajya.milktracker.common.presentation.theme.DIMENSIONS_16DP
import com.swarajya.milktracker.common.presentation.theme.DIMENSIONS_24DP
import com.swarajya.milktracker.common.presentation.theme.DIMENSIONS_32DP
import com.swarajya.milktracker.common.presentation.theme.DIMENSIONS_40DP
import com.swarajya.milktracker.common.presentation.theme.DIMENSIONS_60DP
import com.swarajya.milktracker.common.presentation.theme.DIMENSIONS_8DP
import com.swarajya.milktracker.tracker.presentation.viewmodel.TrackerViewModel

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMilkBottomSheet(
    trackerViewModel: TrackerViewModel,
    selectedDate: String,
    onDismiss: () -> Unit,
    initialMorningQty: Float = 0.0f,
    initialEveningQty: Float = 0.0f,
    initialPrice: Float = 60.0f,
    onSave: (morningQty: Float, eveningQty: Float, price: Float) -> Unit,
    onDelete: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scrollState = rememberScrollState()

    var morningQty by remember { mutableFloatStateOf(initialMorningQty) }
    var eveningQty by remember { mutableFloatStateOf(initialEveningQty) }
    var pricePerLiter by remember { mutableStateOf(initialPrice.toString()) }

    // Logic: Save is only enabled if at least one quantity is > 0
    val isSaveEnabled = morningQty > 0f || eveningQty > 0f
    val showDeleteIcon = initialMorningQty > 0f || initialEveningQty > 0f

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // Ensures buttons are above the system navigation bar
                .verticalScroll(scrollState) // Makes the sheet scrollable on smaller screens or when keyboard is up
                .padding(
                    start = DIMENSIONS_24DP,
                    end = DIMENSIONS_24DP,
                    top = DIMENSIONS_8DP,
                    bottom = DIMENSIONS_16DP
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.log_milk_for, selectedDate),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                if (showDeleteIcon) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Log",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(DIMENSIONS_24DP))

            QuantitySelector(
                label = stringResource(id = R.string.morning),
                quantity = morningQty,
                onQuantityChange = { morningQty = it },
                trackerViewModel = trackerViewModel
            )

            Spacer(modifier = Modifier.height(DIMENSIONS_16DP))

            QuantitySelector(
                label = stringResource(id = R.string.evening),
                quantity = eveningQty,
                onQuantityChange = { eveningQty = it },
                trackerViewModel = trackerViewModel
            )
            Spacer(modifier = Modifier.height(DIMENSIONS_24DP))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(DIMENSIONS_24DP))

            OutlinedTextField(
                value = pricePerLiter,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                        pricePerLiter = newValue
                        trackerViewModel.logPriceChangeClickEvent(pricePerLiter)
                    }
                },
                label = { Text(stringResource(id = R.string.price_per_liter)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(DIMENSIONS_32DP))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DIMENSIONS_16DP)
            ) {
                OutlinedButton(
                    onClick = {
                        trackerViewModel.logCancelClickEvent()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        modifier = Modifier.padding(vertical = DIMENSIONS_8DP),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Button(
                    onClick = {
                        val finalPrice = pricePerLiter.toFloatOrNull() ?: initialPrice
                        onSave(morningQty, eveningQty, finalPrice)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    enabled = isSaveEnabled
                ) {
                    Text(
                        text = stringResource(id = R.string.save_log),
                        modifier = Modifier.padding(vertical = DIMENSIONS_8DP),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun QuantitySelector(
    label: String,
    quantity: Float,
    onQuantityChange: (Float) -> Unit,
    trackerViewModel: TrackerViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            FilledTonalIconButton(
                onClick = {
                    trackerViewModel.logQuantityChangeClickEvent(AnalyticsConstants.Params.PARAM_BUTTON_MINUS)
                    if (quantity > 0f) onQuantityChange(quantity - 0.5f)

                },
                modifier = Modifier.size(DIMENSIONS_40DP)
            ) {
                Text(AppConstants.MINUS, style = MaterialTheme.typography.titleLarge)
            }

            Text(
                text = stringResource(id = R.string.quantity_liters, quantity),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .width(DIMENSIONS_60DP)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )

            FilledTonalIconButton(
                onClick = {
                    trackerViewModel.logQuantityChangeClickEvent(AnalyticsConstants.Params.PARAM_BUTTON_PLUS)
                    onQuantityChange(quantity + 0.5f)
                },
                modifier = Modifier.size(DIMENSIONS_40DP)
            ) {
                Text(AppConstants.PLUS, style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}
