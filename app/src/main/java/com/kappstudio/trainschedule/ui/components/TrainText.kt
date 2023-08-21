package com.kappstudio.trainschedule.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.kappstudio.trainschedule.R
import com.kappstudio.trainschedule.domain.model.Train
import com.kappstudio.trainschedule.util.TrainType
import com.kappstudio.trainschedule.util.localize

@Composable
fun TrainText(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
    style: TextStyle = TextStyle.Default,
    train: Train,
) {
    Text(
        modifier = modifier,
        text = (when (train.number) {
            "1", "2" -> stringResource(id = R.string.tour_train)
            else -> {
                TrainType.getName(train.typeCode)
                    ?.let { stringResource(it) }
            }
        } ?: train.name.localize()) + " ${train.number}",
        style = style,
        fontSize = fontSize
    )
}