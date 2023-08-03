package com.kappstudio.trainschedule.ui.navigation

import com.kappstudio.trainschedule.ui.navigation.DestinationsArgs.CAN_TRANSFER_BOOLEAN
import com.kappstudio.trainschedule.ui.navigation.DestinationsArgs.DATE_STRING
import com.kappstudio.trainschedule.ui.navigation.DestinationsArgs.TIME_STRING
import com.kappstudio.trainschedule.ui.navigation.DestinationsArgs.TIME_TYPE_INT
import com.kappstudio.trainschedule.ui.navigation.DestinationsArgs.TRAIN_TYPE_INT


object Destinations {
    const val PARENT_ROUTE = "parent"
    const val HOME_ROUTE = "home"
    const val STATION_ROUTE = "station"
    const val TRIPS_ROUTE = "trips"
}

object DestinationsWithArgs {
    const val TRIPS =
        "${Destinations.TRIPS_ROUTE}/{$DATE_STRING}/{$TIME_STRING}/{$TIME_TYPE_INT}/{$TRAIN_TYPE_INT}/{$CAN_TRANSFER_BOOLEAN}"
}

private object DestinationsArgs {
    const val DATE_STRING = "date"
    const val TIME_STRING = "time"
    const val TIME_TYPE_INT = "timeType"
    const val TRAIN_TYPE_INT = "trainType"
    const val CAN_TRANSFER_BOOLEAN = "canTransfer"
}