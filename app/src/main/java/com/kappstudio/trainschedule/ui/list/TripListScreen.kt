package com.kappstudio.trainschedule.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import com.kappstudio.trainschedule.domain.model.Name
import com.kappstudio.trainschedule.domain.model.Station
import com.kappstudio.trainschedule.domain.model.Stop
import com.kappstudio.trainschedule.domain.model.TrainSchedule
import com.kappstudio.trainschedule.ui.components.ErrorLayout
import com.kappstudio.trainschedule.ui.components.TimeText
import com.kappstudio.trainschedule.ui.components.TrainText
import com.kappstudio.trainschedule.util.LoadingStatus
import com.kappstudio.trainschedule.util.TrainType
import com.kappstudio.trainschedule.util.toggle
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun TripListScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: TripListViewModel = hiltViewModel(),
    onTripItemClicked: (Trip, String) -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState()
    val currentPath = viewModel.currentPath.collectAsState()
    val loadingState = viewModel.loadingState

    Scaffold(
        topBar = {
            TrainTopAppBar(
                title = currentPath.value.getTitle(),
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
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when (loadingState) {

                LoadingStatus.Loading -> {
                    CircularProgressIndicator()
                }

                LoadingStatus.Done -> {
                    if (uiState.value.trips.isNotEmpty()) {
                        TripColumn(
                            modifier = Modifier.fillMaxSize(),
                            trips = uiState.value.trips,
                            date = uiState.value.date,
                            specifiedTimeTrip = uiState.value.specifiedTimeTrip,
                            onTripItemClicked = { onTripItemClicked(it, uiState.value.date) }
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
                items(TrainType.values().toList()) { type ->
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
    specifiedTimeTrip: Trip?,
    date: String,
    onTripItemClicked: (Trip) -> Unit,
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = specifiedTimeTrip?.let {
        trips.indexOf(it)
    } ?: 0)
    val isToday: Boolean = date == LocalDate.now().toString()

    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(
            items = trips,
            key = { trip ->
                trip.trainSchedules
            }
        ) { trip ->
            val currentTime: String = LocalTime.now().toString().take(5)
            TripItem(
                modifier = Modifier.fillMaxSize(),
                trip = trip,
                isLastLeftTrip = trips.lastOrNull { it.departureTime < currentTime } == trip && isToday,
                hasLeft = trip.departureTime < currentTime && isToday,
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
    hasLeft: Boolean,
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

            TrainsLayout(trains = trip.trainSchedules.map { it.train }, hasLeft = hasLeft)
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
            text = trip.departureTime,
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
            text = trip.arrivalTime,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TimeText(minutes = trip.durationMinutes)
            Text(
                text = "$ " + trip.totalPrice,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun TrainsLayout(
    modifier: Modifier = Modifier,
    trains: List<Train>,
    hasLeft: Boolean,
) {
    Row(modifier = modifier) {
        trains.forEach { train ->
            TrainText(train = train)
            if (train != trains.last()) Text(text = " > ")
        }
        Spacer(modifier = Modifier.weight(1f))
        if (hasLeft) {
            Text(
                text = stringResource(R.string.left),
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
                    train = Train("123", Name("Local", "Local")),
                    price = 100,
                    stops = listOf(Stop("19:30", "20:00", Station()))
                )

            ),
            arrivalTime = "19:30",
            departureTime = "21:10",
        ),
        hasLeft = false,
        isLastLeftTrip = false,
        onTripItemClicked = { _ -> }
    )
}