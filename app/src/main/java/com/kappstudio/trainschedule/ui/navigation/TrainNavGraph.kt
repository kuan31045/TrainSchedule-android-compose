package com.kappstudio.trainschedule.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.kappstudio.trainschedule.ui.home.HomeScreen
import com.kappstudio.trainschedule.ui.home.StationScreen
import com.kappstudio.trainschedule.ui.navigation.Destinations.HOME_ROUTE
import com.kappstudio.trainschedule.ui.navigation.Destinations.PARENT_ROUTE
import com.kappstudio.trainschedule.ui.navigation.Destinations.STATION_ROUTE

@Composable
fun TrainNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = PARENT_ROUTE,
        modifier = modifier
    ) {
        navigation(startDestination = HOME_ROUTE, route = PARENT_ROUTE) {

            composable(route = HOME_ROUTE) { backStackEntry ->
                HomeScreen(
                    viewModel = backStackEntry.sharedViewModel(navController = navController),
                    navToSelectStationClicked = { navController.navigate(STATION_ROUTE) },
                    onSearchButtonClicked = { date, time, timeType, trainType, canTransfer -> }
                )
            }
            composable(route = STATION_ROUTE) { backStackEntry ->
                NavType.StringType
                StationScreen(
                    viewModel = backStackEntry.sharedViewModel(navController = navController),
                    navigateBack = { navController.navigateUp() }
                )
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