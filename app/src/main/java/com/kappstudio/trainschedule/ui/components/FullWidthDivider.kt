package com.kappstudio.trainschedule.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FullWidthDivider(modifier: Modifier = Modifier, thickness: Dp = 1.dp) {
    Divider(modifier = modifier
        .fillMaxWidth()
        .layout { measurable, constraints ->
            val placeable = measurable.measure(constraints.copy(
                maxWidth = constraints.maxWidth + 99.dp.roundToPx(), //add the end padding 16.dp
            ))
            layout(placeable.width, placeable.height) {
                placeable.place(0, 0)
            }
        }, thickness = thickness)
}