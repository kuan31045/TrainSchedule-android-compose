package com.kappstudio.trainschedule.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
@Composable
fun RepeatArrowAnim(modifier:Modifier = Modifier){
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        val infiniteTransition = rememberInfiniteTransition(label = "")
        val scale1 = infiniteTransition.animateFloat(
            0.2f,
            1f,
            infiniteRepeatable(
                tween(600), RepeatMode.Reverse,
                initialStartOffset = StartOffset(
                    offsetMillis = 450,
                    StartOffsetType.FastForward
                )
            ), label = ""
        )
        val scale2 = infiniteTransition.animateFloat(
            0.2f,
            1f,
            infiniteRepeatable(
                tween(600), RepeatMode.Reverse,
                initialStartOffset = StartOffset(
                    offsetMillis = 300,
                    StartOffsetType.FastForward
                )
            ), label = ""
        )
        val scale3 = infiniteTransition.animateFloat(
            0.2f,
            1f,
            infiniteRepeatable(
                tween(600), RepeatMode.Reverse,
                initialStartOffset = StartOffset(
                    offsetMillis = 150,
                    StartOffsetType.FastForward
                )
            ), label = ""
        )
        Column {
            Arrow(scale1)
            Arrow(scale2)
            Arrow(scale3)
        }
    }
}

@Composable
fun Arrow(scale: State<Float>) {
    Box(
        Modifier
            .size(20.dp)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
    ) {
        Icon(
            imageVector =  Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}
