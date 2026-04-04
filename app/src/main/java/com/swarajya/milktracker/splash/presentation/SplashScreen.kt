package com.swarajya.milktracker.splash.presentation

import android.window.SplashScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.swarajya.milktracker.R
import com.swarajya.milktracker.common.presentation.theme.DIMENSIONS_16DP
import com.swarajya.milktracker.common.presentation.theme.DIMENSIONS_2DP
import com.swarajya.milktracker.common.presentation.theme.DIMENSIONS_8DP
import kotlinx.coroutines.delay

private const val SPLASH_SCREEN_DELAY = 2000L

@Composable
fun SplashScreen(
    onNavigateNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isLoadingVisible by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(key1 = true) {
        delay(SPLASH_SCREEN_DELAY)
        isLoadingVisible = false
        onNavigateNext()
    }
    Box {
        Image(
            painter = painterResource(id = R.drawable.milk_tracker_bg),
            contentDescription = null,
            modifier = modifier.fillMaxSize().blur(DIMENSIONS_8DP),
            contentScale = ContentScale.FillBounds
        )

       if(isLoadingVisible)
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
@Preview
fun SplashScreenPreview() {
    SplashScreen(onNavigateNext = {})
}
