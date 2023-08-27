package com.kappstudio.trainschedule.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun LoadingDot(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val infiniteTransition = rememberInfiniteTransition(label = "")
        val scale1 = infiniteTransition.animateFloat(
            0.2f,
            1f,
            // No offset for the 1st animation
            infiniteRepeatable(tween(600), RepeatMode.Reverse), label = ""
        )
        val scale2 = infiniteTransition.animateFloat(
            0.2f,
            1f,
            infiniteRepeatable(
                tween(600), RepeatMode.Reverse,
                // Offsets the 2nd animation by starting from 150ms of the animation
                // This offset will not be repeated.
                initialStartOffset = StartOffset(offsetMillis = 150, StartOffsetType.FastForward)
            ), label = ""
        )
        val scale3 = infiniteTransition.animateFloat(
            0.2f,
            1f,
            infiniteRepeatable(
                tween(600), RepeatMode.Reverse,
                // Offsets the 3rd animation by starting from 300ms of the animation. This
                // offset will be not repeated.
                initialStartOffset = StartOffset(offsetMillis = 300, StartOffsetType.FastForward)
            ), label = ""
        )
        Row {
            Dot(scale1)
            Dot(scale2)
            Dot(scale3)
        }
    }
}

// This is an infinite progress indicator with 3 pulsing dots that grow and shrink.
@Composable
fun Dot(scale: State<Float>) {
    Box(
        Modifier
            .padding(5.dp)
            .size(20.dp)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .background(MaterialTheme.colorScheme.outline, shape = CircleShape)
    )
}