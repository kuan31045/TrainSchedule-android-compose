package com.kappstudio.trainschedule.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.kappstudio.trainschedule.util.localize
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import com.kappstudio.trainschedule.domain.model.Name
import com.kappstudio.trainschedule.ui.components.ErrorLayout
import com.kappstudio.trainschedule.util.LoadingStatus
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripListScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: TripListViewModel = hiltViewModel(),
    onTripItemClicked: (trains: List<String>, transfers: List<String>) -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState()
    val currentPath = viewModel.currentPath.collectAsState()
    val loadingState = viewModel.loadingState

    Scaffold(
        topBar = {
            TrainTopAppBar(
                title = "${currentPath.value.departureStation.name.localize()} " +
                        "➝ ${currentPath.value.arrivalStation.name.localize()}",
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
                    IconButton(onClick = {}) {
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
                LoadingStatus.Done -> {
                    if (uiState.value.trips.isNotEmpty()) {
                        TripColumn(
                            modifier = Modifier.fillMaxSize(),
                            trips = uiState.value.trips,
                            date = uiState.value.date,
                            specifiedTimeTrip = uiState.value.specifiedTimeTrip,
                            onTripItemClicked = { trains, transfers ->
                                onTripItemClicked(trains, transfers)
                            }
                        )
                    } else {
                        Text(text = stringResource(id = R.string.not_find_route))
                    }
                }

                LoadingStatus.Loading -> {
                    CircularProgressIndicator()
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
        }
    }
}


@Composable
fun TripColumn(
    modifier: Modifier = Modifier,
    trips: List<Trip>,
    specifiedTimeTrip: Trip?,
    date: String,
    onTripItemClicked: (trains: List<String>, transfers: List<String>) -> Unit,
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
                trip.trains
            }
        ) { trip ->
            val currentTime: String = LocalTime.now().toString().take(5)
            TripItem(
                modifier = Modifier.fillMaxSize(),
                trip = trip,
                isLastLeftTrip = trips.lastOrNull { it.departureTime < currentTime } == trip && isToday,
                hasLeft = trip.departureTime < currentTime && isToday,
                onTripItemClicked = { trains, transfers -> onTripItemClicked(trains, transfers) }
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
    onTripItemClicked: (trains: List<String>, transfers: List<String>) -> Unit,
) {
    Card(
        modifier = modifier,
        onClick = {
            onTripItemClicked(trip.trains.map { it.number }, trip.transfers.map { it.id })
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
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
                        modifier = Modifier.padding(2.dp),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 4.dp),
                            painter = painterResource(R.drawable.ic_time),
                            contentDescription = stringResource(R.string.time_desc)
                        )
                        Text(
                            text = when {
                                trip.durationMinutes < 60 -> "${trip.durationMinutes} ${
                                    stringResource(
                                        id = R.string.minute
                                    )
                                }"

                                trip.durationMinutes % 60 == 0 -> "${trip.durationMinutes / 60} ${
                                    stringResource(
                                        id = R.string.hour
                                    )
                                }"

                                else -> stringResource(
                                    id = R.string.time_format,
                                    (trip.durationMinutes / 60),
                                    (trip.durationMinutes % 60)
                                )
                            },
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Text(
                        text = "$ " + trip.totalPrice,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Divider(
                thickness = 0.6.dp,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            Row {
                trip.trains.forEach { train ->
                    Text(text = train.name.localize().split("(").first() + train.number)
                    if (train != trip.trains.last()) Text(text = " > ")
                }
                Spacer(modifier = Modifier.weight(1f))
                if (hasLeft) {
                    Text(
                        text = stringResource(R.string.left),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (isLastLeftTrip) {
        Divider(
            thickness = 2.dp,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Preview
@Composable
fun TripItemPreview() {
    TripItem(
        trip = Trip(
            trains = listOf(
                Train("123", Name("Local", "Local")),
                Train("123", Name("Ziqiang", "Ziqiang"))
            ),
            arrivalTime = "19:30",
            departureTime = "21:10",
        ),
        hasLeft = false,
        isLastLeftTrip = true,
        onTripItemClicked = { _, _ -> }
    )
}