package com.kappstudio.trainschedule.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.kappstudio.trainschedule.data.local.Converters

@Entity(tableName = "lines")
@TypeConverters(Converters::class)
data class LineEntity(
    @PrimaryKey val id: String,
    val stations: List<StationEntity>,
)