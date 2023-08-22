package com.kappstudio.trainschedule.ui.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
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
import com.kappstudio.trainschedule.ui.components.RoundRectRoute
import com.kappstudio.trainschedule.ui.components.SmallStationPoint
import com.kappstudio.trainschedule.ui.components.TimeText
import com.kappstudio.trainschedule.ui.components.TrainLargeTopAppBar
import com.kappstudio.trainschedule.ui.components.TrainText
import com.kappstudio.trainschedule.ui.components.pullrefreshm3.PullRefreshIndicator
import com.kappstudio.trainschedule.ui.components.pullrefreshm3.pullRefresh
import com.kappstudio.trainschedule.ui.components.pullrefreshm3.rememberPullRefreshState
import com.kappstudio.trainschedule.util.TrainStatus
import com.kappstudio.trainschedule.util.TrainType
import com.kappstudio.trainschedule.util.calDurationMinutes
import com.kappstudio.trainschedule.util.localize
import com.kappstudio.trainschedule.util.toDateWeekFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit,
    viewModel: TripDetailViewModel,
    onTrainButtonClicked: (String, String) -> Unit,
    onHomeButtonClicked: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState()
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
            TripBody(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                date = uiState.value.date.toDateWeekFormatter(),
                trip = uiState.value.trip,
                onTrainButtonClicked = { onTrainButtonClicked(it, uiState.value.date) }
            )
        }

        PullRefreshIndicator(
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .padding(top = 100.dp),
            refreshing = refreshing,
            state = refreshState,
        )
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
        LazyColumn {
            item {
                Row(modifier=Modifier.padding(bottom = 4.dp)) {
                    Text(text = date, fontSize = 16.sp)
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = stringResource(id = R.string.price, trip.totalPrice)
                    )
                }
            }

            items(trip.trainSchedules) { schedule ->
                ScheduleItem(
                    schedule = schedule,
                    onTrainButtonClicked = {

                        val trainShortName = (when (schedule.train.number) {
                            "1", "2" -> context.resources.getString(R.string.tour_train)
                            else -> {
                                TrainType.getName(schedule.train.typeCode)
                                    ?.let { context.resources.getString(it) }
                            }
                        } ?: schedule.train.name.localize()) + "-${schedule.train.number}"

                        onTrainButtonClicked(trainShortName)
                    }
                )
                if (schedule != trip.trainSchedules.last()) {
                    TransferLayout()
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

            BigStationLayout(text = schedule.stops.first().station.name.localize())

            Button(onClick = onTrainButtonClicked) {
                TrainText(train = schedule.train)
            }

            TrainTimeLayout(train = schedule.train, departureTime = schedule.departureTime)

            StopsLayout(stops = schedule.stops)

            Row(verticalAlignment = Alignment.CenterVertically) {
                BigStationLayout(
                    modifier = Modifier.weight(1f),
                    text = schedule.stops.last().station.name.localize()
                )
                Text(text = schedule.arrivalTime, style = MaterialTheme.typography.titleLarge)
            }

            Divider(thickness = 1.dp)
        }
    }
}

@Composable
fun TrainTimeLayout(modifier: Modifier = Modifier, train: Train, departureTime: String) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier
                .padding(start = 4.dp)
                .weight(1f),
            text = when (train.status) {
                TrainStatus.DELAY -> stringResource(
                    id = R.string.delay, train.delayTime ?: 1
                )

                else -> stringResource(id = train.status.status)
            },
            color = when (train.status) {
                TrainStatus.EXPECTED -> MaterialTheme.colorScheme.outline
                TrainStatus.ON_TIME -> com.kappstudio.trainschedule.ui.theme.on_time
                TrainStatus.DELAY -> MaterialTheme.colorScheme.error
            }
        )
        Text(text = departureTime, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun BigStationLayout(
    modifier: Modifier = Modifier,
    text: String,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        BigStationPoint()
        Text(
            modifier = Modifier
                .padding(start = 2.dp)
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(placeable.width, placeable.height) {
                        placeable.place(-24.dp.roundToPx(), 0)
                    }
                },
            text = text,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun StopsLayout(
    modifier: Modifier = Modifier,
    stops: List<Stop>,
) {
    var isExpand by rememberSaveable { mutableStateOf(false) }

    Divider(thickness = 1.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = stops.size > 2) { isExpand = !isExpand },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (stops.size > 2) {
            Icon(
                imageVector = if (isExpand) {
                    Icons.Default.KeyboardArrowUp
                } else {
                    Icons.Default.KeyboardArrowDown
                },
                contentDescription = if (isExpand) {
                    stringResource(id = R.string.collapse_desc)
                } else {
                    stringResource(id = R.string.expand_desc)
                }
            )
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
            minutes = calDurationMinutes(
                stops.first().departureTime,
                stops.last().arrivalTime
            )
        )
    }
    AnimatedVisibility(
        visible = isExpand
    ) {
        Column {
            stops.drop(1).dropLast(1).forEach {
                PassStationItem(text = it.station.name.localize())
            }
        }
    }
    if (!isExpand) {
        Divider(thickness = 1.dp)
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
    val stop = Stop(
        arrivalTime = "08:00",
        departureTime = "08:05",
        Station(name = Name(en = "Taipei", zh = "Taipei"))
    )

    ScheduleItem(
        modifier = Modifier,
        schedule = TrainSchedule(
            train = Train(number = "9527", typeCode = 3),
            price = 500,
            stops = listOf(stop, stop, stop)
        ),
        onTrainButtonClicked = { }
    )
}