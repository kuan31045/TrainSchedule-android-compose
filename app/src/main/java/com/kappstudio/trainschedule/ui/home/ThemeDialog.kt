package com.kappstudio.trainschedule.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.kappstudio.trainschedule.AppTheme
import com.kappstudio.trainschedule.R
import com.kappstudio.trainschedule.ui.components.FullWidthDivider

@Composable
fun ThemeDialog(
    modifier: Modifier = Modifier,
    closeDialog: () -> Unit,
    selectedTheme: AppTheme,
    onThemePreferenceChanged: (Int) -> Unit,
    isDynamic: Boolean,
    onDynamicPreferenceChanged: (Boolean) -> Unit,
) {


    AlertDialog(
        containerColor = MaterialTheme.colorScheme.onSecondary,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        onDismissRequest = closeDialog,
        title = {
            Text(text = stringResource(id = R.string.appearance))
        },
        text = {
            Column {

                AppTheme.values().forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedTheme == theme,
                                onClick = { onThemePreferenceChanged(theme.ordinal) },
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = selectedTheme == theme,
                            onClick = { onThemePreferenceChanged(theme.ordinal) },
                        )
                        Text(
                            stringResource(id = theme.stringRes),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                FullWidthDivider(modifier = Modifier.padding(top=16.dp))


            }
        },
        confirmButton = {
            Row(
                modifier = modifier.clickable { onDynamicPreferenceChanged(!isDynamic) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f),
                    text = stringResource(id = R.string.dynamic_color),
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = isDynamic,
                    onCheckedChange = { onDynamicPreferenceChanged(!isDynamic) }
                )
            }

        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    )
}