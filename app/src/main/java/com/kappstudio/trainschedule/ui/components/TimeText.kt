package com.kappstudio.trainschedule.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kappstudio.trainschedule.R

@Composable
fun TimeText(
    modifier: Modifier = Modifier,
    minutes: Int,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(16.dp)
                .padding(end = 4.dp),
            painter = painterResource(R.drawable.ic_time),
            contentDescription = stringResource(R.string.time_desc)
        )

        Text(
            text = when {
                minutes < 60 -> "$minutes ${
                    stringResource(
                        id = R.string.minute
                    )
                }"

                minutes % 60 == 0 -> "${minutes / 60} ${
                    stringResource(
                        id = R.string.hour
                    )
                }"

                else -> stringResource(
                    id = R.string.time_format,
                    (minutes / 60),
                    (minutes % 60)
                )
            },
            style = MaterialTheme.typography.labelLarge
        )
    }
}