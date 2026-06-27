package com.swarajya.milktracker.settings.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swarajya.milktracker.R
import com.swarajya.milktracker.common.presentation.theme.DIMENSIONS_16DP
import com.swarajya.milktracker.common.presentation.theme.DIMENSIONS_8DP

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val themePreference by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val isNotificationsEnabled by viewModel.isNotificationsEnabled.collectAsStateWithLifecycle()
    val defaultPrice by viewModel.defaultPrice.collectAsStateWithLifecycle()
    val currentThemeIsDark = themePreference ?: isSystemInDarkTheme()

    val scrollState = rememberScrollState()
    var priceText by remember(defaultPrice) { mutableStateOf(defaultPrice.toString()) }

    LaunchedEffect(Unit) {
        viewModel.logScreenViewEvent()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(DIMENSIONS_16DP)
    ) {
        // Theme Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = DIMENSIONS_8DP),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.dark_theme),
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = currentThemeIsDark,
                onCheckedChange = { viewModel.onThemeChanged(it) }
            )
        }

        // Notification Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = DIMENSIONS_8DP),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.notifications),
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = isNotificationsEnabled,
                onCheckedChange = { viewModel.onNotificationToggle(it) }
            )
        }

        Spacer(modifier = Modifier.height(DIMENSIONS_8DP))

        // Price Configuration
        OutlinedTextField(
            value = priceText,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                    priceText = newValue
                    newValue.toFloatOrNull()?.let { viewModel.onPriceChanged(it) }
                }
            },
            label = { Text(stringResource(id = R.string.price_per_liter)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = DIMENSIONS_16DP))

        // App Version
        ListItem(
            headlineContent = { Text(text = stringResource(id = R.string.app_version)) },
            supportingContent = { Text(text = viewModel.appVersion) },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null
                )
            }
        )

        // Contact Us
        ListItem(
            headlineContent = { Text(text = stringResource(id = R.string.contact_us)) },
            supportingContent = { Text(text = stringResource(id = R.string.contact_email)) },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null
                )
            },
            modifier = Modifier.padding(top = DIMENSIONS_8DP)
        )
    }
}
