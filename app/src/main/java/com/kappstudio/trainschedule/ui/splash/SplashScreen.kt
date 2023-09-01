package com.kappstudio.trainschedule.ui.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import com.kappstudio.trainschedule.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavToHomeScreen: () -> Unit) {
    var startAnimation by rememberSaveable { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1200
        ), label = ""
    )

    LaunchedEffect(key1 = startAnimation) {
        startAnimation = true
        delay(1400)
        onNavToHomeScreen()
    }
    Splash(alpha = alphaAnim.value)
}


@Composable
fun Splash(modifier: Modifier = Modifier, alpha: Float) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    )
    {
        Icon(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.splash_icon_size))
                .alpha(alpha = alpha),
            painter = painterResource(id = R.drawable.ic_train),
            contentDescription = "Logo",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}