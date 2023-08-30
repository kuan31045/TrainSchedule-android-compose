package com.kappstudio.trainschedule.ui.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kappstudio.trainschedule.R
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.kappstudio.trainschedule.domain.model.Name
import com.kappstudio.trainschedule.domain.model.Station
import com.kappstudio.trainschedule.domain.model.Stop
import com.kappstudio.trainschedule.domain.model.Train
import com.kappstudio.trainschedule.domain.model.TrainSchedule
import com.kappstudio.trainschedule.domain.model.Trip
import com.kappstudio.trainschedule.ui.components.BigStationPoint
import com.kappstudio.trainschedule.ui.components.ErrorLayout
import com.kappstudio.trainschedule.ui.components.ExpandIcon
import com.kappstudio.trainschedule.ui.components.LoadingDot
import com.kappstudio.trainschedule.ui.components.RoundRectRoute
import com.kappstudio.trainschedule.ui.components.SmallStationPoint
import com.kappstudio.trainschedule.ui.components.TimeText
import com.kappstudio.trainschedule.ui.components.TrainLargeTopAppBar
import com.kappstudio.trainschedule.ui.components.pullrefreshm3.PullRefreshIndicator
import com.kappstudio.trainschedule.ui.components.pullrefreshm3.pullRefresh
import com.kappstudio.trainschedule.ui.components.pullrefreshm3.rememberPullRefreshState
import com.kappstudio.trainschedule.util.LoadingStatus
import com.kappstudio.trainschedule.util.TrainType
import com.kappstudio.trainschedule.util.dateWeekFormatter
import com.kappstudio.trainschedule.util.localize
import com.kappstudio.trainschedule.util.timeFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit,
    viewModel: TripDetailViewModel,
    onTrainButtonClicked: (String) -> Unit,
    onHomeButtonClicked: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState()
    val dateTimeState = viewModel.dateTimeState.collectAsState()
    val loadingState = viewModel.loadingState

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    fun refresh() = refreshScope.launch {
        refreshing = true
        delay(300)
        viewModel.fetchTrainsDelayTime()
        refreshing = false
    }

    val refreshState = rememberPullRefreshState(refreshing, ::refresh)

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(state = refreshState)
    ) {
        Scaffold(
            topBar = {
                TrainLargeTopAppBar(
                    title = uiState.value.trip.path.getTitle(),
                    navigateUp = onNavigateUp,
                    scrollBehavior = scrollBehavior,
                    actions = {
                        IconButton(onClick = onHomeButtonClicked) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = stringResource(id = R.string.to_home_desc)
                            )
                        }
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(id = R.string.more_desc)
                            )
                        }
                    }
                )
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { innerPadding ->

            when (loadingState) {
                LoadingStatus.Loading -> {
                    LoadingDot()
                }

                LoadingStatus.Done -> {
                    TripBody(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp),
                        date = dateTimeState.value.format(dateWeekFormatter),
                        trip = uiState.value.trip,
                        onTrainButtonClicked = { onTrainButtonClicked(it) }
                    )
                }

                is LoadingStatus.Error -> {
                    ErrorLayout(
                        modifier = Modifier.padding(top = 160.dp, start = 16.dp, end = 16.dp),
                        text = loadingState.error,
                        retry = { viewModel.fetchStop() }
                    )
                }
            }
        }
        if (loadingState == LoadingStatus.Done) {
            PullRefreshIndicator(
                modifier = Modifier
                    .align(alignment = Alignment.TopCenter)
                    .padding(top = 100.dp),
                refreshing = refreshing,
                state = refreshState,
            )
        }

    }
}

@Composable
fun TripBody(
    modifier: Modifier = Modifier,
    date: String,
    trip: Trip,
    onTrainButtonClicked: (String) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp),

            ) {
            item {
                Row(modifier = Modifier.padding(bottom = 4.dp)) {
                    Text(text = date, fontSize = 18.sp)
                    Text(
                        modifier = Modifier
                            .padding(start = 8.dp),
                        text = trip.startTime.format(timeFormatter) + "-" + trip.endTime.format(
                            timeFormatter
                        ),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            items(trip.trainSchedules) { schedule ->
                ScheduleItem(
                    schedule = schedule,
                    onTrainButtonClicked = {

                        val trainShortName = (when (schedule.train.number) {
                            "1", "2" -> context.resources.getString(R.string.tour_train)
                            else -> context.resources.getString(schedule.train.type.trainName)
                        }) + "-${schedule.train.number}"

                        onTrainButtonClicked(trainShortName)
                    }
                )
                if (schedule != trip.trainSchedules.last()) {
                    TransferLayout()
                }
            }

            item {
                if (trip.totalPrice != 0) {
                    Text(
                        text = stringResource(id = R.string.price, trip.totalPrice)
                    )
                }
            }
        }
    }
}

@Composable
fun TransferLayout(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row {
            Icon(
                modifier = Modifier.padding(start = 8.dp),
                painter = painterResource(id = R.drawable.ic_transfer_within_a_station),
                contentDescription = null
            )
            Text(modifier = Modifier.padding(start = 24.dp), text = "轉乘")
        }
        Divider(modifier = Modifier.padding(start = 56.dp, end = 8.dp), thickness = 1.dp)
    }
}

@Composable
fun ScheduleItem(
    modifier: Modifier = Modifier,
    schedule: TrainSchedule,
    lateMinutes: Int = 0,
    onTrainButtonClicked: () -> Unit,
) {
    var heightIs by remember { mutableStateOf(0.dp) }
    val localDensity = LocalDensity.current

    Box(modifier = modifier.padding(horizontal = 8.dp, vertical = 16.dp)) {

        RoundRectRoute(modifier = Modifier.padding(vertical = 4.dp), height = heightIs)

        Column(
            modifier = Modifier
                .padding(start = 48.dp)
                .onGloballyPositioned { coordinates ->
                    heightIs = with(localDensity) { coordinates.size.height.toDp() }
                },
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            BigStationLayout(
                station = schedule.stops.first().station.name.localize(),
                time = schedule.stops.first().departureTime.format(
                    timeFormatter
                )
            )



            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
               Button(onClick = onTrainButtonClicked) {
                   Text(
                       modifier = modifier,
                       text = when (schedule.train.number) {
                           "1", "2" -> stringResource(id = R.string.tour_train)
                           else -> stringResource(schedule.train.type.trainName)
                       } + " ${schedule.train.number}",
                       fontSize = 16.sp
                   )
                }
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = when(schedule.train.delay)  {
                        null -> ""
                        0-> stringResource(id = R.string.on_time)
                        else -> stringResource(id = R.string.delay, schedule.train.delay)
                    },
                    color = when(schedule.train.delay)  {
                        null -> MaterialTheme.colorScheme.onPrimary
                        0-> com.kappstudio.trainschedule.ui.theme.on_time
                        else -> MaterialTheme.colorScheme.error
                    }
                )
            }


            StopsLayout(stops = schedule.stops)


            BigStationLayout(
                station = schedule.stops.last().station.name.localize(),
                time = schedule.getEndTime().format(timeFormatter)
            )


            Divider(thickness = 1.dp)
        }
    }
}


@Composable
fun BigStationLayout(
    modifier: Modifier = Modifier,
    station: String,
    time: String,
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        BigStationPoint()
        Text(
            modifier = Modifier
                .padding(start = 2.dp)
                .weight(1f)
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(placeable.width, placeable.height) {
                        placeable.place(-24.dp.roundToPx(), 0)
                    }
                },
            text = station,
            style = MaterialTheme.typography.titleLarge
        )
        Text(text = time, style = MaterialTheme.typography.titleLarge)

    }
}

@Composable
fun StopsLayout(
    modifier: Modifier = Modifier,
    stops: List<Stop>,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier) {
        Divider(thickness = 1.dp)
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .defaultMinSize(minHeight = 36.dp)
                .fillMaxWidth()
                .clickable(enabled = stops.size > 2) { isExpanded = !isExpanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (stops.size > 2) {
                ExpandIcon(isExpanded = isExpanded)
            }
            Text(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .weight(1f),
                text = stringResource(
                    id = R.string.stops, stops.size - 1
                ) + " ",
                fontSize = 16.sp
            )
            TimeText(
                minutes =

                Duration.between(stops.first().departureTime, stops.last().arrivalTime).toMinutes()

            )
        }
        AnimatedVisibility(
            visible = isExpanded
        ) {
            Column {
                stops.drop(1).dropLast(1).forEach {
                    PassStationItem(text = it.station.name.localize())
                }
            }
        }
        if (!isExpanded) {
            Divider(thickness = 1.dp)
        }
    }
}

@Composable
fun PassStationItem(modifier: Modifier = Modifier, text: String) {
    Row(
        modifier = modifier.padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SmallStationPoint()
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Preview
@Composable
fun ScheduleItemPreview() {
    val stops = (1..5).map {
        Stop(
            station = Station(
                id = it.toString(),
                name = Name(en = "station $it", zh = "車站 $it")
            )
        )
    }

    ScheduleItem(
        modifier = Modifier,
        schedule = TrainSchedule(
            train = Train(number = "9527", type = TrainType.NEW_TC),
            price = 500,
            stops = stops
        ),
        onTrainButtonClicked = { }
    )
}