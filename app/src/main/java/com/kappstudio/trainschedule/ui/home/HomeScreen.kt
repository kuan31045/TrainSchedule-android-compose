package com.kappstudio.trainschedule.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kappstudio.trainschedule.R
import com.kappstudio.trainschedule.ui.components.SwapButton
import com.kappstudio.trainschedule.util.localize

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navToSelectStationClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState = viewModel.uiState.collectAsState()
    val pathState = viewModel.pathState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        HomeStationLayout(
            departureStation = pathState.value.departureStation.name.localize(),
            arrivalStation = pathState.value.arrivalStation.name.localize(),
            onStationButtonClicked = {
                viewModel.changeSelectedStation(it)
                navToSelectStationClicked()
            },
            onSwapButtonClicked = { viewModel.swapPath() }
        )
    }
}

@Composable
fun HomeStationLayout(
    departureStation: String,
    arrivalStation: String,
    onStationButtonClicked: (SelectedStationType) -> Unit,
    onSwapButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        ToStationScreenButton(
            desc = stringResource(R.string.from_text),
            station = departureStation,
            onClicked = { onStationButtonClicked(SelectedStationType.DEPARTURE) }
        )
        SwapButton(onClicked = onSwapButtonClicked)
        ToStationScreenButton(
            desc = stringResource(R.string.to_text),
            station = arrivalStation,
            onClicked = { onStationButtonClicked(SelectedStationType.ARRIVAL) }
        )
    }
}

@Composable
fun ToStationScreenButton(
    desc: String,
    station: String,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = desc, color = MaterialTheme.colorScheme.secondary)
        ElevatedButton(
            modifier = Modifier.sizeIn(minWidth = 120.dp),
            shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner_size)),
            onClick = onClicked,
            elevation = ButtonDefaults.buttonElevation(dimensionResource(R.dimen.button_elevation))
        ) {
            Text(
                modifier = Modifier.padding(vertical = 4.dp),
                text = station,
                style = MaterialTheme.typography.titleLarge.localize(),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Preview
@Composable
fun PreviewHomeStationLayout() {
    HomeStationLayout(
        departureStation = "New Taipei",
        arrivalStation = "Kaohsiung",
        onSwapButtonClicked = {},
        onStationButtonClicked = {}
    )
}