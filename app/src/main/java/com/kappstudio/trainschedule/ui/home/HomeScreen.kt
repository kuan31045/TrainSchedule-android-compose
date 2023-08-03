package com.kappstudio.trainschedule.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kappstudio.trainschedule.R
import com.kappstudio.trainschedule.ui.TrainTopAppBar
import com.kappstudio.trainschedule.ui.components.GradientButton
import com.kappstudio.trainschedule.ui.components.SegmentedControl
import com.kappstudio.trainschedule.ui.components.SwapButton
import com.kappstudio.trainschedule.util.dateFormatter
import com.kappstudio.trainschedule.util.localize
import com.kappstudio.trainschedule.util.timeFormatter
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navToSelectStationClicked: () -> Unit,
    onSearchButtonClicked: (
        date: String, time: String, timeType: Int,
        trainType: Int, canTransfer: Boolean
    ) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState = viewModel.uiState.collectAsState()
    val pathState = viewModel.pathState.collectAsState()

    Scaffold(
        topBar = {
            TrainTopAppBar(
                title = stringResource(R.string.app_name),
                canNavigateBack = false,
                navigateUp = {},
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            HomeStationLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                departureStation = pathState.value.departureStation.name.localize(),
                arrivalStation = pathState.value.arrivalStation.name.localize(),
                onStationButtonClicked = {
                    viewModel.changeSelectedStation(it)
                    navToSelectStationClicked()
                },
                onSwapButtonClicked = { viewModel.swapPath() }
            )

            DateTimeLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 24.dp),
                date = uiState.value.date,
                time = uiState.value.time,
                timeType = uiState.value.timeType,
                confirmTime = { date, time, ordinal ->
                    viewModel.setDateTime(date, time, ordinal)
                }
            )

            TrainTypeSelection(
                modifier = Modifier.padding(horizontal = 24.dp),
                selectedIndex = uiState.value.trainMainType.ordinal,
                onTypeSelected = { viewModel.selectTrainType(it) }
            )

            TransSwitch(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                checked = uiState.value.canTransfer,
                onCheckedChange = { viewModel.setTransfer() }
            )

            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            )

            SearchButton(
                onClicked = {
                    onSearchButtonClicked(
                        uiState.value.date.toString(),
                        uiState.value.time.toString(),
                        uiState.value.timeType.ordinal,
                        uiState.value.trainMainType.ordinal,
                        uiState.value.canTransfer
                    )
                }
            )
        }
    }
}

@Composable
fun HomeStationLayout(
    departureStation: String,
    arrivalStation: String,
    onStationButtonClicked: (SelectedType) -> Unit,
    onSwapButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        ToStationScreenButton(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            desc = stringResource(R.string.from),
            station = departureStation,
            onClicked = { onStationButtonClicked(SelectedType.DEPARTURE) }
        )
        SwapButton(onClicked = onSwapButtonClicked)
        ToStationScreenButton(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            desc = stringResource(R.string.to),
            station = arrivalStation,
            onClicked = { onStationButtonClicked(SelectedType.ARRIVAL) }
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
        Text(
            text = desc,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelLarge
        )
        ElevatedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner_size)),
            onClick = onClicked,
            elevation = ButtonDefaults.buttonElevation(dimensionResource(R.dimen.button_elevation))
        ) {
            Text(
                modifier = Modifier.padding(vertical = 4.dp),
                text = station,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun DateTimeLayout(
    modifier: Modifier = Modifier,
    date: LocalDate,
    time: LocalTime,
    timeType: SelectedType,
    confirmTime: (LocalDate, LocalTime, Int) -> Unit
) {
    var shouldShowDialog by rememberSaveable { mutableStateOf(false) }

    Row(modifier = modifier) {
        ElevatedButton(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner_size)),
            onClick = { shouldShowDialog = true },
            elevation = ButtonDefaults.buttonElevation(dimensionResource(R.dimen.button_elevation))
        ) {
            Text(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .weight(1f),
                text = "${date.format(dateFormatter)}   ${time.format(timeFormatter)}",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                modifier = Modifier.padding(vertical = 4.dp),
                text = stringResource(id = timeType.text),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }

    //-----Open Dialog------------------------------------------------------------------------------
    if (shouldShowDialog) {
        DateTimeDialog(
            closeDialog = { shouldShowDialog = false },
            defaultDate = date,
            defaultTime = time,
            defaultSelectedIndex = timeType.ordinal,
            confirmTime = { p1, p2, p3 ->
                confirmTime(p1, p2, p3)
            }
        )
    }
}

@Composable
fun TrainTypeSelection(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    onTypeSelected: (Int) -> Unit
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(bottom = 4.dp),
            text = stringResource(id = R.string.train_type),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelLarge
        )
        SegmentedControl(
            items = TrainMainType.values().map { stringResource(id = it.text) },
            selectedIndex = selectedIndex,
            onItemSelected = onTypeSelected
        )
    }
}

@Composable
fun TransSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: () -> Unit,
) {
    Row(
        modifier = modifier.clickable { onCheckedChange() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(id = R.string.accept_transfer),
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = checked,
            onCheckedChange = { onCheckedChange() }
        )
    }
}

@Composable
fun SearchButton(
    modifier: Modifier = Modifier,
    onClicked: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = dimensionResource(R.dimen.surface_shadow_elevation_16)
    ) {
        GradientButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 8.dp, end = 8.dp),
            text = stringResource(id = R.string.search),
            textColor = Color.White,
            gradient = Brush.horizontalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.surfaceTint,
                    MaterialTheme.colorScheme.primary
                )
            ),
            onClicked = onClicked
        )
    }
}

@Preview
@Composable
fun HomeStationLayoutPreview() {
    HomeStationLayout(
        departureStation = "Taipei",
        arrivalStation = "Hsinchu",
        onSwapButtonClicked = {},
        onStationButtonClicked = {}
    )
}

@Preview
@Composable
fun TrainTypeSelectionPreview() {
    TrainTypeSelection(
        selectedIndex = 0,
        onTypeSelected = {}
    )
}

@Preview
@Composable
fun DateTimeLayoutPreview() {
    DateTimeLayout(
        date = LocalDate.MAX,
        time = LocalTime.MAX,
        timeType = SelectedType.DEPARTURE,
        confirmTime = { _, _, _ -> }
    )
}