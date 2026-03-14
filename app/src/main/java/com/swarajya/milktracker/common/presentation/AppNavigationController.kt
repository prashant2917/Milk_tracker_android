package com.swarajya.milktracker.common.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.swarajya.milktracker.common.navigation.MonthlyCalendar
import com.swarajya.milktracker.common.navigation.Splash
import com.swarajya.milktracker.splash.presentation.SplashScreen
import com.swarajya.milktracker.tracker.presentation.MonthlyMilkCalendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigationController(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Splash,
        modifier = modifier
    ) {
        composable<Splash> {
            SplashScreen(
                onNavigateNext = {
                    navController.navigate(MonthlyCalendar) {
                        popUpTo(Splash) { inclusive = true }
                    }
                }
            )
        }
        composable<MonthlyCalendar> {
            MonthlyMilkCalendar()
        }
    }
}
