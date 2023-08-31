package com.kappstudio.trainschedule.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kappstudio.trainschedule.R
import com.kappstudio.trainschedule.ui.TrainTopAppBar
import com.kappstudio.trainschedule.ui.components.GradientButton
import com.kappstudio.trainschedule.ui.components.SegmentedControl
import com.kappstudio.trainschedule.ui.components.SwapButton
import com.kappstudio.trainschedule.util.dateWeekFormatter
import com.kappstudio.trainschedule.util.getNowDateTime
import com.kappstudio.trainschedule.util.localize
import com.kappstudio.trainschedule.util.timeFormatter
import java.time.LocalDateTime

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    onToStationButtonClicked: () -> Unit,
    onSearchButtonClicked: (
        timeType: Int,
        trainType: Int,
        canTransfer: Boolean,
    ) -> Unit,
    onToFavoriteButtonClicked: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState()
    val pathState = viewModel.pathState.collectAsState()
    val dateState = viewModel.dateTimeState.collectAsState()
    val stationState = viewModel.stationState.collectAsState()
    val lineState = viewModel.lineState.collectAsState()

    val appThemeState = viewModel.appThemeState.collectAsState()
    val dynamicColorState = viewModel.dynamicColorState.collectAsState()

    var isMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var shouldShowThemeDialog by rememberSaveable { mutableStateOf(false) }
    var shouldShowPolicyDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TrainTopAppBar(
                title = stringResource(R.string.app_name),
                canNavigateBack = false,
                actions = {
                    IconButton(onClick = onToFavoriteButtonClicked) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = stringResource(id = R.string.favorite_title)
                        )
                    }
                    IconButton(
                        onClick = { isMenuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(id = R.string.more_desc)
                        )
                    }

                    //-----Menu---------------------------------------------------------------------
                    DropdownMenu(
                        modifier = Modifier.background(MaterialTheme.colorScheme.onPrimary),
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false }
                    ) {

                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.appearance)) },
                            onClick = {
                                isMenuExpanded = false
                                shouldShowThemeDialog = true
                            },
                            leadingIcon = {
                                Icon(
                                    painterResource(id = R.drawable.ic_brightness_medium),
                                    contentDescription = null
                                )
                            })

                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.privacy_policy)) },
                            onClick = {
                                isMenuExpanded = false
                                shouldShowPolicyDialog = true
                            },
                            leadingIcon = {
                                Icon(
                                    painterResource(id = R.drawable.ic_policy),
                                    contentDescription = null
                                )
                            })
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            //-----Theme Dialog---------------------------------------------------------------------
            if (shouldShowThemeDialog) {
                ThemeDialog(
                    closeDialog = { shouldShowThemeDialog = false },
                    selectedTheme = appThemeState.value,
                    onThemePreferenceChanged = { viewModel.saveAppThemePreference(it) },
                    isDynamic = dynamicColorState.value,
                    onDynamicPreferenceChanged = { viewModel.saveDynamicColorPreference(it) }
                )
            }

            //-----Policy Dialog--------------------------------------------------------------------
            if (shouldShowPolicyDialog) {
                PolicyDialog(closeDialog = { shouldShowPolicyDialog = false })
            }

            HomeStationLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 8.dp, end = 8.dp),
                departureStation = pathState.value.departureStation.name.localize(),
                arrivalStation = pathState.value.arrivalStation.name.localize(),
                onStationButtonClicked = {
                    viewModel.changeSelectedStation(it)
                    onToStationButtonClicked()
                },
                onSwapButtonClicked = { viewModel.swapPath() }
            )

            DateTimeLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 24.dp),
                dateTime = dateState.value,
                timeType = uiState.value.timeType,
                confirmTime = { dateTime, ordinal ->
                    viewModel.saveDateTime(dateTime, ordinal)
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
                    .padding(vertical = 16.dp),
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
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        ToStationScreenButton(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            desc = stringResource(R.string.from_station),
            station = departureStation,
            onClicked = { onStationButtonClicked(SelectedType.DEPARTURE) }
        )
        SwapButton(modifier = Modifier.padding(top = 48.dp), onClicked = onSwapButtonClicked)
        ToStationScreenButton(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            desc = stringResource(R.string.to_station),
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
    modifier: Modifier = Modifier,
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
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun DateTimeLayout(
    modifier: Modifier = Modifier,
    dateTime: LocalDateTime,
    timeType: SelectedType,
    confirmTime: (LocalDateTime, Int) -> Unit,
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
                text = "${dateTime.format(dateWeekFormatter)}   ${dateTime.format(timeFormatter)}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                modifier = Modifier.padding(vertical = 4.dp),
                text = stringResource(id = timeType.text),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }

    //-----Date Time Dialog-------------------------------------------------------------------------
    if (shouldShowDialog) {
        DateTimeDialog(
            closeDialog = { shouldShowDialog = false },
            defaultDateTime = dateTime,
            defaultSelectedIndex = timeType.ordinal,
            confirmTime = { dateTime, selectedType ->
                confirmTime(dateTime, selectedType)
            }
        )
    }
}

@Composable
fun TrainTypeSelection(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    onTypeSelected: (Int) -> Unit,
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
            modifier = Modifier.padding(start = 24.dp),
            text = stringResource(id = R.string.accept_transfer),
            fontSize = 16.sp
        )
        Switch(
            modifier = Modifier.padding(end = 24.dp),
            checked = checked,
            onCheckedChange = { onCheckedChange() }
        )
    }
}

@Composable
fun SearchButton(
    modifier: Modifier = Modifier,
    onClicked: () -> Unit,
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
        dateTime = getNowDateTime(),
        timeType = SelectedType.DEPARTURE,
        confirmTime = { _, _ -> }
    )
}