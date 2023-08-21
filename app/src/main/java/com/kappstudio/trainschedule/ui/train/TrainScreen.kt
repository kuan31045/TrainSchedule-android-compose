package com.kappstudio.trainschedule.ui.train

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kappstudio.trainschedule.R
import com.kappstudio.trainschedule.ui.components.TrainLargeTopAppBar
import com.kappstudio.trainschedule.ui.list.TripItemTopLayout
import com.kappstudio.trainschedule.util.dateWeekFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainScreen(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit,
    onHomeButtonClicked: () -> Unit,
    viewModel: TrainViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState()
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            TrainLargeTopAppBar(
                title = uiState.value.trainShortName,
                navigateUp = onNavigateUp,
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = onHomeButtonClicked) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = stringResource(id = R.string.to_home_desc)
                        )
                    }}
                    )
                },
                modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    Text(text = uiState.value.date)
                    LazyColumn {}

                }
            }
        }