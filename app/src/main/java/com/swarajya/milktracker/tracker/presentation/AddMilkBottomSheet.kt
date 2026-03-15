package com.swarajya.milktracker.tracker.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.swarajya.milktracker.R
import com.swarajya.milktracker.common.constants.AppConstants
import com.swarajya.milktracker.common.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMilkBottomSheet(
    selectedDate: String,
    onDismiss: () -> Unit,
    initialMorningQty: Float = 0.0f,
    initialEveningQty: Float = 0.0f,
    initialPrice: Float = 60.0f,
    onSave: (morningQty: Float, eveningQty: Float, price: Float) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    
    var morningQty by remember { mutableFloatStateOf(initialMorningQty) }
    var eveningQty by remember { mutableFloatStateOf(initialEveningQty) }
    var pricePerLiter by remember { mutableStateOf(initialPrice.toString()) }

    // Logic: Save is only enabled if at least one quantity is > 0
    val isSaveEnabled = morningQty > 0f || eveningQty > 0f

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DIMENSIONS_24DP)
                .padding(bottom = DIMENSIONS_48DP),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.log_milk_for, selectedDate), 
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(DIMENSIONS_24DP))

            QuantitySelector(
                label = stringResource(id = R.string.morning), 
                quantity = morningQty, 
                onQuantityChange = { morningQty = it }
            )
            
            Spacer(modifier = Modifier.height(DIMENSIONS_16DP))
            
            QuantitySelector(
                label = stringResource(id = R.string.evening), 
                quantity = eveningQty, 
                onQuantityChange = { eveningQty = it }
            )
            Spacer(modifier = Modifier.height(DIMENSIONS_24DP))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(DIMENSIONS_24DP))

            OutlinedTextField(
                value = pricePerLiter,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                        pricePerLiter = newValue
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
                    onClick = onDismiss,
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

@Composable
fun QuantitySelector(
    label: String, 
    quantity: Float, 
    onQuantityChange: (Float) -> Unit
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
                onClick = { if (quantity > 0f) onQuantityChange(quantity - 0.5f) },
                modifier = Modifier.size(DIMENSIONS_40DP)
            ) {
                Text(AppConstants.MINUS, style = MaterialTheme.typography.titleLarge)
            }
            
            Text(
                text = stringResource(id = R.string.quantity_liters, quantity), 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(DIMENSIONS_60DP).wrapContentWidth(Alignment.CenterHorizontally)
            )
            
            FilledTonalIconButton(
                onClick = { onQuantityChange(quantity + 0.5f) },
                modifier = Modifier.size(DIMENSIONS_40DP)
            ) {
                Text(AppConstants.PLUS, style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}
