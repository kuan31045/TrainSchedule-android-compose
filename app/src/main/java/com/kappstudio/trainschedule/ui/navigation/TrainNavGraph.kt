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
import androidx.navigation.navArgument
import com.kappstudio.trainschedule.ui.home.HomeScreen
import com.kappstudio.trainschedule.ui.home.StationScreen
import com.kappstudio.trainschedule.ui.list.TripListScreen
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.CAN_TRANSFER_BOOLEAN
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.DATE_STRING
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.TIME_STRING
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.TIME_TYPE_INT
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.TRAIN_TYPE_INT

@Composable
fun TrainNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.PARENT.route,
        modifier = modifier
    ) {
        navigation(startDestination = Screen.HOME.route, route = Screen.PARENT.route) {

            composable(route = Screen.HOME.route) { backStackEntry ->
                HomeScreen(
                    viewModel = backStackEntry.sharedViewModel(navController = navController),
                    navToSelectStationClicked = { navController.navigate(Screen.STATION.route) },
                    onSearchButtonClicked = { date, time, timeType, trainType, canTransfer ->
                        navController.navigate(
                            Screen.TRIPS.route
                                    + "/${date}" + "/${time}" + "/${timeType}" + "/${trainType}" + "/${canTransfer}"
                        )

                    }
                )
            }

            composable(
                route = Screen.STATION.route
            ) { backStackEntry ->
                StationScreen(
                    viewModel = backStackEntry.sharedViewModel(navController = navController),
                    navigateBack = { navController.navigateUp() }
                )
            }

            composable(route = RoutesWithArgs.TRIPS,
                arguments = listOf(
                    navArgument(DATE_STRING) { type = NavType.StringType },
                    navArgument(TIME_STRING) { type = NavType.StringType },
                    navArgument(TIME_TYPE_INT) { type = NavType.IntType },
                    navArgument(TRAIN_TYPE_INT) { type = NavType.IntType },
                    navArgument(CAN_TRANSFER_BOOLEAN) { type = NavType.BoolType }
                )) {
                TripListScreen(
                    navigateBack = { navController.navigateUp() },
                    onTripItemClicked = { trains, transfers -> }
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