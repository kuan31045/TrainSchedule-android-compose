package com.kappstudio.trainschedule.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kappstudio.trainschedule.data.local.entity.StationEntity

@Database(entities = [StationEntity::class], version = 1)
abstract class TrainDatabase : RoomDatabase() {

    abstract val stationDao: StationDao
}