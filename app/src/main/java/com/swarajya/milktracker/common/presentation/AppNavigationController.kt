package com.swarajya.milktracker.common.presentation

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.swarajya.milktracker.R
import com.swarajya.milktracker.common.navigation.MonthlyCalendar
import com.swarajya.milktracker.common.navigation.Settings
import com.swarajya.milktracker.common.navigation.Splash
import com.swarajya.milktracker.settings.presentation.SettingsScreen
import com.swarajya.milktracker.splash.presentation.SplashScreen
import com.swarajya.milktracker.tracker.presentation.MonthlyMilkCalendar

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigationController(modifier: Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showTopBar = currentDestination?.hasRoute<Splash>() == false
    val isSettingsScreen = currentDestination?.hasRoute<Settings>() == true

    // Notification Permission Launcher for Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            // Optionally handle permission result (e.g., update a state or show a message)
        }
    )

    Scaffold(
        topBar = {
            if (showTopBar) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = if (isSettingsScreen) stringResource(id = R.string.settings) 
                                   else stringResource(id = R.string.app_name),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        if (isSettingsScreen) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    },
                    actions = {
                        if (!isSettingsScreen) {
                            IconButton(onClick = { navController.navigate(Settings) }) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                        actionIconContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Splash,
            modifier = modifier.padding(innerPadding)
        ) {
            composable<Splash> {
                SplashScreen(
                    onNavigateNext = {
                        navController.navigate(MonthlyCalendar) {
                            popUpTo<Splash> { inclusive = true }
                        }
                    }
                )
            }
            composable<MonthlyCalendar> {
                // Request permission when the calendar screen is first shown (after splash)
                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
                MonthlyMilkCalendar()
            }
            composable<Settings> {
                SettingsScreen()
            }
        }
    }
}
