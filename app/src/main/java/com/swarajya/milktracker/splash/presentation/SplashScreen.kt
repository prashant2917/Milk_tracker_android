package com.swarajya.milktracker.splash.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.swarajya.milktracker.R
import com.swarajya.milktracker.common.presentation.theme.DIMENSIONS_0DP
import com.swarajya.milktracker.common.presentation.theme.DIMENSIONS_8DP
import kotlinx.coroutines.delay

private const val SPLASH_SCREEN_DELAY = 2000L

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onNavigateNext: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    var isLoadingVisible by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(key1 = true) {
        viewModel.logSplashScreenOpenEvent()
        delay(SPLASH_SCREEN_DELAY)
        isLoadingVisible = false
        onNavigateNext()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.milk_tracker_bg),
            contentDescription = null,
            modifier = modifier
                .fillMaxSize()
                .blur(if (isLoadingVisible) DIMENSIONS_8DP else DIMENSIONS_0DP),
            contentScale = ContentScale.FillBounds
        )

        if (isLoadingVisible) {
            CircularProgressIndicator(
                modifier = modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
