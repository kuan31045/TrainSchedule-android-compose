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
import androidx.navigation.navOptions
import com.kappstudio.trainschedule.ui.detail.TripDetailScreen
import com.kappstudio.trainschedule.ui.detail.TripDetailViewModel
import com.kappstudio.trainschedule.ui.favorite.FavoriteScreen
import com.kappstudio.trainschedule.ui.home.HomeScreen
import com.kappstudio.trainschedule.ui.home.StationScreen
import com.kappstudio.trainschedule.ui.list.TripListScreen
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.CAN_TRANSFER_BOOLEAN
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.TIME_TYPE_INT
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.TRAIN_STRING
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.TRAIN_TYPE_INT
import com.kappstudio.trainschedule.ui.train.TrainScreen

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
                    onToStationButtonClicked = { navController.navigate(Screen.STATION.route) },
                    onSearchButtonClicked = { timeType, trainType, canTransfer ->
                        navController.navigate(
                            Screen.TRIPS.route
                                    + "/$timeType" + "/$trainType" + "/$canTransfer"
                        )
                    },
                    onToFavoriteButtonClicked = {
                        navController.navigate(Screen.FAVORITE.route)
                    }
                )
            }

            composable(route = Screen.STATION.route) { backStackEntry ->
                StationScreen(
                    viewModel = backStackEntry.sharedViewModel(navController = navController),
                    navigateBack = { navController.navigateUp() }
                )
            }

            composable(route = Screen.FAVORITE.route) {
                FavoriteScreen(navigateBack = { navController.navigateUp() })
            }

            composable(route = RoutesWithArgs.TRIPS,
                arguments = listOf(
                    navArgument(TIME_TYPE_INT) { type = NavType.IntType },
                    navArgument(TRAIN_TYPE_INT) { type = NavType.IntType },
                    navArgument(CAN_TRANSFER_BOOLEAN) { type = NavType.BoolType }
                )) { backStackEntry ->

                val viewModel: TripDetailViewModel =
                    backStackEntry.sharedViewModel(navController = navController)

                TripListScreen(
                    navigateBack = { navController.navigateUp() },
                    onTripItemClicked = { trip, isTransferTrip->
                        viewModel.setTrip(trip, isTransferTrip)
                        navController.navigate(Screen.DETAIL.route)
                    }
                )
            }

            composable(route = Screen.DETAIL.route) { backStackEntry ->
                TripDetailScreen(
                    viewModel = backStackEntry.sharedViewModel(navController = navController),
                    onNavigateUp = { navController.navigateUp() },
                    onTrainButtonClicked = {train->
                        navController.navigate(Screen.TRAIN.route + "/$train")
                    },
                    onHomeButtonClicked = { navController.navigateToHome() }
                )
            }

            composable(
                route = RoutesWithArgs.TRAIN,
                arguments = listOf(
                    navArgument(TRAIN_STRING) { type = NavType.StringType },
                )
            ) {
                TrainScreen(
                    onNavigateUp = { navController.navigateUp() },
                    onHomeButtonClicked = { navController.navigateToHome() }
                )
            }
        }
    }
}

fun NavController.navigateToHome() {
    this.navigate(Screen.HOME.route,
        navOptions {
            popUpTo(Screen.HOME.route)
            launchSingleTop = true
        }
    )
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}