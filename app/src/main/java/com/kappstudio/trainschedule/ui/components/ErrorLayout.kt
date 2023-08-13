package com.kappstudio.trainschedule.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kappstudio.trainschedule.R

@Composable
fun ErrorLayout(modifier: Modifier = Modifier, text: String, retry: () -> Unit) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(id = R.string.loading_fail), style = MaterialTheme.typography.titleLarge)
        Text(text = text, style = MaterialTheme.typography.titleMedium)
        Button(onClick = retry) {
            Text(stringResource(id = R.string.retry))
        }
    }
}