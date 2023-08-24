package com.kappstudio.trainschedule.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kappstudio.trainschedule.R

@Composable
fun ExpandButton(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    onClicked: () -> Unit,
) {
    IconButton(modifier = modifier, onClick = onClicked) {
        ExpandIcon(isExpanded = isExpanded)
    }
}

@Composable
fun ExpandIcon(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
) {
    Icon(
        modifier = modifier,
        imageVector = if (isExpanded) {
            Icons.Default.KeyboardArrowUp
        } else {
            Icons.Default.KeyboardArrowDown
        },
        contentDescription = if (isExpanded) {
            stringResource(id = R.string.collapse_desc)
        } else {
            stringResource(id = R.string.expand_desc)
        }
    )
}