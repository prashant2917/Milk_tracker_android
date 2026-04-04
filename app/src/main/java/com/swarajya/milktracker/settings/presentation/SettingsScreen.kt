package com.swarajya.milktracker.settings.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
    val currentThemeIsDark = themePreference ?: isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
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
