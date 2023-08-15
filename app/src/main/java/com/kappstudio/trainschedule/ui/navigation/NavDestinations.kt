package com.kappstudio.trainschedule.ui.navigation

import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.CAN_TRANSFER_BOOLEAN
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.DATE_STRING
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.TIME_STRING
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.TIME_TYPE_INT
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.TRAIN_TYPE_INT

enum class Screen(val route: String) {
    PARENT(route = "parent"),
    HOME(route = "home"),
    STATION(route = "station"),
    TRIPS(route = "trips"),
    FAVORITE(route = "favorite"),
}

object NavigationArgs {
    const val DATE_STRING = "date"
    const val TIME_STRING = "time"
    const val TIME_TYPE_INT = "timeType"
    const val TRAIN_TYPE_INT = "trainType"
    const val CAN_TRANSFER_BOOLEAN = "canTransfer"
}

object RoutesWithArgs {
    val TRIPS =
        "${Screen.TRIPS.route}/{$DATE_STRING}/{$TIME_STRING}/{$TIME_TYPE_INT}/{$TRAIN_TYPE_INT}/{$CAN_TRANSFER_BOOLEAN}"
}