package com.kappstudio.trainschedule.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.kappstudio.trainschedule.R

@Composable
fun PolicyDialog(modifier: Modifier = Modifier, closeDialog: () -> Unit) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.onSecondary,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 48.dp),
        onDismissRequest = closeDialog,
        title = {
            Text(text = stringResource(id = R.string.privacy_policy))
        },
        text = {
            Text(
                text = stringResource(id = R.string.privacy_policy_full),
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            )
        },
        confirmButton = {
            TextButton(onClick = closeDialog) {
                Text(stringResource(id = R.string.ok))
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    )
}