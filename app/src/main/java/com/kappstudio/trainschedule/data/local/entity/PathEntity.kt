package com.kappstudio.trainschedule.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paths")
data class PathEntity(
    @PrimaryKey val id: String,
    @Embedded(prefix = "departure_station_")
    val departureStation: StationEntity,
    @Embedded(prefix = "arrival_station_")
    val arrivalStation: StationEntity,
)

