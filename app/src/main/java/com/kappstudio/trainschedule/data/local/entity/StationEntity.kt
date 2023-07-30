package com.kappstudio.trainschedule.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kappstudio.trainschedule.domain.model.Name

@Entity(tableName = "stations")
data class StationEntity(
    @PrimaryKey val id: String,
    @Embedded val name: Name,
    @Embedded(prefix = "county_name_")
    val county: Name
)