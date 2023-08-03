package com.kappstudio.trainschedule

import androidx.compose.foundation.layout.padding
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.kappstudio.trainschedule.ui.home.HomeScreen
import com.kappstudio.trainschedule.ui.home.StationScreen

const val PARENT_ROUTE = "Parent"

enum class Screen(val route: String) {
    Home(route = "Home"),
    Station(route = "Station"),
}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainAppBar(
    title: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    shouldShowCheckedIcon: Boolean,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_desc),
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            if (shouldShowCheckedIcon) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.checked_desc),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = modifier
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainApp(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(),
) {
    val navController = rememberNavController()
    val titleState = viewModel.titleState.collectAsState()
    val screenState = viewModel.currentScreenState.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    viewModel.setCurrentScreen(
        Screen.valueOf(
            backStackEntry?.destination?.route?.split("/")?.get(0)
                ?: Screen.Home.name,
        )
    )
    Scaffold(
        topBar = {
            TrainAppBar(
                title = titleState.value,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                shouldShowCheckedIcon = screenState.value == Screen.Station
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = PARENT_ROUTE,
            modifier = modifier.padding(innerPadding)
        ) {
            navigation(startDestination = Screen.Home.route, route = PARENT_ROUTE) {

                composable(route = Screen.Home.route) { backStackEntry ->
                    HomeScreen(
                        viewModel = backStackEntry.sharedViewModel(navController = navController),
                        navToSelectStationClicked = { navController.navigate(Screen.Station.route) },
                        onSearchButtonClicked = { p1, p2, p3, p4->}
                    )
                }

                composable(route = Screen.Station.route) { backStackEntry ->
                    NavType.StringType
                    StationScreen(
                        viewModel = backStackEntry.sharedViewModel(navController = navController)
                    )
                }
            }
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}

@Preview
@Composable
fun TrainAppBarPreview() {
    TrainAppBar(title = "台鐵時刻表", canNavigateBack = true, navigateUp = { }, shouldShowCheckedIcon = false)
}
