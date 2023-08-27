package com.kappstudio.trainschedule.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kappstudio.trainschedule.R
import kotlinx.coroutines.launch

@Composable
fun BigStationPoint(modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.onPrimary

    Canvas(modifier = modifier
        .layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) {
                placeable.place(-48.dp.roundToPx(), 0)
            }
        }
        .padding(dimensionResource(id = R.dimen.big_circle_padding))
        .size(dimensionResource(id = R.dimen.big_circle_size)),
        onDraw = { drawCircle(color) })
}

@Composable
fun SmallStationPoint(modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.secondaryContainer
    val circleSize = remember {
        Animatable(0f)
    }

    LaunchedEffect(circleSize) {
        launch {
            circleSize.animateTo(1f, animationSpec = tween(1600))
        }
    }
    Canvas(modifier = modifier
        .layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) {
                placeable.place(-52.dp.roundToPx(), 0)
            }
        }
        .padding(dimensionResource(id = R.dimen.small_circle_padding))
        .size(dimensionResource(id = R.dimen.small_circle_size)),
        onDraw = {
            scale(circleSize.value) {
                drawCircle(color)
            }
        })
}

@Composable
fun RoundRectRoute(
    modifier: Modifier = Modifier,
    height: Dp,
) {
    val color = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier
        .padding(top = 0.5.dp)
        .size(
            width = dimensionResource(id = R.dimen.round_rect_width),
            height = height - 25.5.dp
        ),
        onDraw = {
            drawRoundRect(
                color = color, cornerRadius = CornerRadius(x = 99.dp.toPx(), y = 99.dp.toPx())
            )
        }
    )
}