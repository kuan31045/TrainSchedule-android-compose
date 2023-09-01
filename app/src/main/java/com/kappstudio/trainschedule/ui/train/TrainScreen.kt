package com.kappstudio.trainschedule.ui.train

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kappstudio.trainschedule.R
import com.kappstudio.trainschedule.ui.components.ErrorLayout
import com.kappstudio.trainschedule.ui.components.TrainLargeTopAppBar
import com.kappstudio.trainschedule.util.LoadingStatus
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.kappstudio.trainschedule.domain.model.Name
import com.kappstudio.trainschedule.domain.model.Station
import com.kappstudio.trainschedule.domain.model.StationLiveBoard
import com.kappstudio.trainschedule.domain.model.Stop
import com.kappstudio.trainschedule.domain.model.Train
import com.kappstudio.trainschedule.ui.components.ExpandButton
import com.kappstudio.trainschedule.ui.components.FullWidthDivider
import com.kappstudio.trainschedule.ui.components.LoadingDot
import com.kappstudio.trainschedule.ui.components.RepeatArrowAnim
import com.kappstudio.trainschedule.ui.components.TrainIcon
import com.kappstudio.trainschedule.util.TrainFlag
import com.kappstudio.trainschedule.util.TrainType
import com.kappstudio.trainschedule.util.dateWeekFormatter
import com.kappstudio.trainschedule.util.getNowDateTime
import com.kappstudio.trainschedule.util.localize
import com.kappstudio.trainschedule.util.timeFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainScreen(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit,
    onHomeButtonClicked: () -> Unit,
    viewModel: TrainViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState()
    val dateTimeState = viewModel.dateTimeState.collectAsState()
    // Initial collapsed
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState(
                initialHeightOffset = -248f
            )
        )
    val loadingState = viewModel.loadingState

    Scaffold(
        topBar = {
            TrainLargeTopAppBar(
                title = uiState.value.trainShortName.replace("-", " "),
                navigateUp = onNavigateUp,
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = onHomeButtonClicked) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = stringResource(id = R.string.to_home_desc)
                        )
                    }
                }
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {

            when (loadingState) {
                LoadingStatus.Loading -> {
                    LoadingDot()
                }

                LoadingStatus.Done -> {
                    Column(Modifier.fillMaxSize()) {
                        TrainInfoLayout(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            train = uiState.value.trainSchedule.train,
                            date = dateTimeState.value.format(dateWeekFormatter)
                        )
                        StopsBody(
                            modifier = Modifier.fillMaxSize(),
                            stops = uiState.value.trainSchedule.stops,
                            liveBoards = uiState.value.liveBoards,
                            trainIndex = uiState.value.trainIndex,
                            currentTime = uiState.value.currentTime
                        )
                    }
                }

                is LoadingStatus.Error -> {
                    ErrorLayout(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        text = stringResource(id = loadingState.errorStringRes),
                        retry = { viewModel.getTrain() }
                    )
                }
            }
        }
    }
}

@Composable
fun StopsBody(
    modifier: Modifier = Modifier,
    stops: List<Stop>,
    trainIndex: Int,
    liveBoards: List<StationLiveBoard>,
    currentTime: LocalDateTime,
) {
    val listState = rememberLazyListState()
    var alreadyRolled by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(bottom = 20.dp),
    ) {
        if (trainIndex != 0 && !alreadyRolled) {
            coroutineScope.launch {
                delay(100)
                alreadyRolled = true
                listState.animateScrollToItem(index = trainIndex)
            }
        }
        itemsIndexed(stops) { index, stop ->
            StopItem(
                stop = stop,
                index = index,
                trainIndex = trainIndex,
                isLast = index == stops.size - 1,
                liveBoard = liveBoards.firstOrNull { it.stationId == stop.station.id },
                currentTime = currentTime
            )
            Divider()
        }
    }
}

@Composable
fun StopItem(
    modifier: Modifier = Modifier,
    stop: Stop,
    index: Int,
    trainIndex: Int,
    isLast: Boolean,
    liveBoard: StationLiveBoard? = null,
    currentTime: LocalDateTime,
) {
    val textColor = if (index < trainIndex) {
        MaterialTheme.colorScheme.outline
    } else {
        Color.Unspecified
    }

    val routeColor = if ((index < trainIndex) || (isLast && currentTime > stop.arrivalTime)) {
        MaterialTheme.colorScheme.outline
    } else {
        MaterialTheme.colorScheme.primary
    }

    var heightIs by remember { mutableStateOf(0.dp) }
    val localDensity = LocalDensity.current

    Row(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                heightIs = with(localDensity) { coordinates.size.height.toDp() }
            }
            .padding(end = 16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            Spacer(
                modifier = Modifier
                    .size(width = 25.dp, height = heightIs)
                    .drawBehind { drawRect(routeColor) }
            )
            if (index == trainIndex && liveBoard != null) {
                if (currentTime < stop.arrivalTime.plusMinutes(liveBoard.delay)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TrainIcon()
                        RepeatArrowAnim()
                    }
                } else {
                    TrainIcon(modifier = Modifier.align(Alignment.Center))
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(start = 24.dp)
                .weight(1f)
        ) {
            Text(
                text = stop.station.name.localize(),
                color = textColor,
                fontSize = 20.sp,
                maxLines = 1
            )
            liveBoard?.let {
                Text(
                    text = if (it.delay > 0) {
                        stringResource(
                            id = R.string.delay, it.delay
                        )
                    } else {
                        stringResource(R.string.on_time)
                    },
                    color = if (it.delay > 0) {
                        MaterialTheme.colorScheme.error
                    } else {
                        com.kappstudio.trainschedule.ui.theme.on_time
                    }
                )
            }
        }

        Column(
            Modifier
                .padding(vertical = 18.dp)
        ) {
            if (isLast) {
                Text(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = textColor,
                    text = stop.arrivalTime.format(timeFormatter),
                    fontSize = 18.sp
                )
            } else {
                Text(
                    text = stop.arrivalTime.format(timeFormatter),
                    color = textColor,
                    fontSize = 18.sp
                )
                Text(
                    text = stop.departureTime.format(timeFormatter),
                    color = textColor,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun TrainInfoLayout(modifier: Modifier = Modifier, train: Train, date: String) {

    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = date)
            Text(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f), text = train.headSign
            )

            ExpandButton(modifier = Modifier.layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.place(12.dp.roundToPx(), 0)
                }
            },
                isExpanded = isExpanded, onClicked = { isExpanded = !isExpanded })
        }
        if (train.flags.isNotEmpty()) {
            FlagLayout(modifier = Modifier.fillMaxWidth(), flags = train.flags)
        }

        AnimatedVisibility(
            visible = isExpanded
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 12.dp),
                text = train.note,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        FullWidthDivider(modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
fun FlagLayout(modifier: Modifier = Modifier, flags: List<TrainFlag>) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        flags.forEach { flag ->
            Text(
                text = stringResource(id = flag.flagName),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            if (flag != flags.last()) {
                Text(text = " | ", color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

@Preview
@Composable
fun TrainInfoPreview() {
    TrainInfoLayout(
        train = Train(
            number = "123",
            headSign = "往高雄",
            type = TrainType.NEW_TC,
            flags = listOf(TrainFlag.DAILY_FLAG, TrainFlag.BIKE_FLAG)
        ), date = "2023-10-10"
    )
}

@Preview
@Composable
fun StopsColumnPreview() {
    val stops = (1..5).map {
        Stop(
            station = Station(
                id = it.toString(),
                name = Name(en = "station $it", zh = "車站 $it")
            )
        )
    }

    StopsBody(
        modifier = Modifier.padding(top = 16.dp),
        stops = stops,
        liveBoards = emptyList(),
        trainIndex = 1,
        currentTime = getNowDateTime()
    )
}