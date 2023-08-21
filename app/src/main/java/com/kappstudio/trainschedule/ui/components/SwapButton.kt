package com.kappstudio.trainschedule.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kappstudio.trainschedule.R

@Composable
fun SwapButton(
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        modifier = modifier.size(dimensionResource(R.dimen.swap_button_size)),
        onClick = onClicked
    ) {
        Icon(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.ic_swap),
            contentDescription = stringResource(R.string.swap_desc),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview
@Composable
fun PreviewSwapButton() {
    SwapButton(onClicked = {})
}