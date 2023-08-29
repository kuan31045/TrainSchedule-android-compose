package com.kappstudio.trainschedule.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kappstudio.trainschedule.R
import com.kappstudio.trainschedule.domain.model.Train
import com.kappstudio.trainschedule.domain.model.Trip
import com.kappstudio.trainschedule.ui.TrainTopAppBar
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.kappstudio.trainschedule.domain.model.Name
import com.kappstudio.trainschedule.domain.model.Station
import com.kappstudio.trainschedule.domain.model.Stop
import com.kappstudio.trainschedule.domain.model.TrainSchedule
import com.kappstudio.trainschedule.ui.components.ErrorLayout
import com.kappstudio.trainschedule.ui.components.LoadingDot
import com.kappstudio.trainschedule.ui.components.TimeText
import com.kappstudio.trainschedule.ui.components.TrainText
import com.kappstudio.trainschedule.util.LoadingStatus
import com.kappstudio.trainschedule.util.TrainType
import com.kappstudio.trainschedule.util.dateWeekFormatter
import com.kappstudio.trainschedule.util.getNowDateTime
import com.kappstudio.trainschedule.util.timeFormatter
import com.kappstudio.trainschedule.util.toggle

@Composable
fun TripListScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: TripListViewModel = hiltViewModel(),
    onTripItemClicked: (Trip, Boolean) -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState()
    val pathState = viewModel.currentPath.collectAsState()
    val loadingState = viewModel.loadingState
    val dateState = viewModel.dateTimeState.collectAsState()
    Scaffold(
        topBar = {
            TrainTopAppBar(
                title = pathState.value.getTitle(),
                canNavigateBack = true,
                navigateUp = navigateBack,
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        if (uiState.value.isFavorite) {
                            Icon(
                                painter = painterResource(R.drawable.ic_star),
                                contentDescription = stringResource(R.string.remove_favorite_desc),
                                tint = MaterialTheme.colorScheme.inversePrimary
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.ic_star_outline),
                                contentDescription = stringResource(R.string.add_favorite_desc),
                            )
                        }
                    }
                    IconButton(onClick = { viewModel.openFilter() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_filter),
                            contentDescription = stringResource(id = R.string.filter_desc),
                            modifier = modifier
                        )
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (loadingState) {

                LoadingStatus.Loading -> {
                    LoadingDot()
                }

                LoadingStatus.Done -> {
                    if (uiState.value.trips.isNotEmpty()) {
                        TripColumn(
                            modifier = Modifier.fillMaxSize(),
                            trips = uiState.value.trips,
                            initialIndex = uiState.value.initialTripIndex,
                            onTripItemClicked = { onTripItemClicked(it, uiState.value.canTransfer) }
                        )

                    } else {
                        Text(text = stringResource(id = R.string.not_find_route))
                    }
                }

                is LoadingStatus.Error -> {
                    ErrorLayout(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        text = loadingState.error,
                        retry = { viewModel.searchTrips() }
                    )
                }
            }

            //-----Filter Bottom Sheet--------------------------------------------------------------
            if (uiState.value.isFiltering) {
                FilterBottomSheet(
                    modifier = Modifier.fillMaxSize(),
                    defaultTypes = uiState.value.filteredTrainTypes,
                    closeBottomSheet = { viewModel.closeFilter(it) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    modifier: Modifier = Modifier,
    defaultTypes: List<TrainType>,
    closeBottomSheet: (List<TrainType>) -> Unit,
) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    var filteredTypes by rememberSaveable { mutableStateOf(defaultTypes) }

    ModalBottomSheet(
        modifier = modifier,
        sheetState = modalBottomSheetState,
        onDismissRequest = { closeBottomSheet(filteredTypes) }
    ) {
        Column {
            Text(
                modifier = Modifier.padding(start = 24.dp, bottom = 16.dp),
                text = stringResource(id = R.string.filter_desc),
                style = MaterialTheme.typography.titleLarge
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(TrainType.values().toList().dropLast(1)) { type ->
                    Row(
                        modifier = Modifier
                            .selectable(
                                selected = type in filteredTypes,
                                onClick = { filteredTypes = filteredTypes.toggle(type) },
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            modifier = Modifier.padding(start = 24.dp),
                            checked = type in filteredTypes,
                            onCheckedChange = { filteredTypes = filteredTypes.toggle(type) },
                        )
                        Text(stringResource(id = type.trainName))
                    }
                }
            }
        }
    }
}

@Composable
fun TripColumn(
    modifier: Modifier = Modifier,
    trips: List<Trip>,
    initialIndex: Int,
    onTripItemClicked: (Trip) -> Unit,
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val currentDateTime = getNowDateTime()

    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(  16.dp)
    ) {
        items(
            items = trips ,

        ) { trip ->
            TripItem(
                modifier = Modifier.fillMaxSize(),
                trip = trip,
                isLastLeftTrip = trips.lastOrNull { it.startTime < currentDateTime } == trip,
                hasDeparted = trip.startTime < currentDateTime,
                onTripItemClicked = { onTripItemClicked(it) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripItem(
    modifier: Modifier = Modifier,
    trip: Trip,
    isLastLeftTrip: Boolean,
    hasDeparted: Boolean,
    onTripItemClicked: (Trip) -> Unit,
) {
    Card(
        modifier = modifier,
        onClick = { onTripItemClicked(trip) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            TripItemTopLayout(trip = trip)

            Divider(
                thickness = 0.6.dp,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            TrainsLayout(trains = trip.trainSchedules.map { it.train }, hasDeparted = hasDeparted)
        }
    }

    if (isLastLeftTrip) {
        Divider(
            thickness = 2.dp,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun TripItemTopLayout(
    modifier: Modifier = Modifier,
    trip: Trip,
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text(
            text = trip.startTime.format(timeFormatter),
            style = MaterialTheme.typography.titleLarge,
            fontSize = 25.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Icon(
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp),
                painter = painterResource(id = R.drawable.ic_arrow),
                contentDescription = null
            )
            Text(
                text = if (trip.transferCount == 0) {
                    stringResource(id = R.string.direct)
                } else {
                    trip.transferCount.toString() + stringResource(id = R.string.transferTimes)
                },
                style = MaterialTheme.typography.labelSmall
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = trip.endTime.format(timeFormatter),
            style = MaterialTheme.typography.titleLarge,
            fontSize = 25.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TimeText(minutes = trip.durationMinutes)
            if(trip.totalPrice!=0){
                Text(
                    text = "$ " + trip.totalPrice,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun TrainsLayout(
    modifier: Modifier = Modifier,
    trains: List<Train>,
    hasDeparted: Boolean,
) {
    Row(modifier = modifier) {
        trains.forEach { train ->
            TrainText(train = train)
            if (train != trains.last()) Text(text = ", ")
        }
        Spacer(modifier = Modifier.weight(1f))
        if (hasDeparted) {
            Text(
                text = stringResource(R.string.departed),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview
@Composable
fun TripItemPreview() {
    TripItem(
        trip = Trip(
            trainSchedules = listOf(
                TrainSchedule(
                    train = Train("123", Name("Local", "Local"), TrainType.NEW_TC),
                    price = 100,
                    stops = listOf(Stop(station = Station()))
                )

            )
        ),
        hasDeparted = false,
        isLastLeftTrip = false,
        onTripItemClicked = { _ -> }
    )
}