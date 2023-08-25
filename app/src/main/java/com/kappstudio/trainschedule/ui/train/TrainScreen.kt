package com.kappstudio.trainschedule.ui.train

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.kappstudio.trainschedule.domain.model.Stop
import com.kappstudio.trainschedule.domain.model.Train
import com.kappstudio.trainschedule.domain.model.TrainSchedule
import com.kappstudio.trainschedule.ui.components.ExpandButton
import com.kappstudio.trainschedule.ui.components.FullWidthDivider
import com.kappstudio.trainschedule.ui.detail.PassStationItem
import com.kappstudio.trainschedule.util.TrainFlag
import com.kappstudio.trainschedule.util.dateWeekFormatter
import com.kappstudio.trainschedule.util.localize

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

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
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
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when (loadingState) {
                LoadingStatus.Loading -> {
                    CircularProgressIndicator()
                }

                LoadingStatus.Done -> {
                    Column(Modifier.fillMaxSize()) {
                        TrainInfoLayout(
                            train = uiState.value.trainSchedule.train,
                            date = dateTimeState.value.format(dateWeekFormatter)
                        )
                    }

                }

                is LoadingStatus.Error -> {
                    ErrorLayout(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        text = loadingState.error,
                        retry = { viewModel.getTrain() }
                    )
                }
            }
        }
    }
}

@Composable
fun StopsColumn(
    modifier: Modifier = Modifier,
    stops: List<Stop>,
    isRunning: Boolean,
) {
    LazyColumn(modifier = modifier) {
        items(items = stops,
            key = { stop ->
                stop.station.id
            }) { stop ->
            Text(text = stop.station.name.localize())

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
            Text(text =  train.headSign )
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
    TrainInfoLayout(train = Train(number = "123", headSign = "往高雄"), date = "2023-10-10")
}