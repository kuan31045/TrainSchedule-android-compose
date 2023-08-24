package com.kappstudio.trainschedule.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kappstudio.trainschedule.data.local.dao.LineDao
import com.kappstudio.trainschedule.data.local.dao.PathDao
import com.kappstudio.trainschedule.data.local.dao.StationDao
import com.kappstudio.trainschedule.data.local.entity.LineEntity
import com.kappstudio.trainschedule.data.local.entity.StationEntity
import com.kappstudio.trainschedule.data.local.entity.PathEntity

@Database(entities = [StationEntity::class, LineEntity::class, PathEntity::class], version = 1)
abstract class TrainDatabase : RoomDatabase() {

    abstract val stationDao: StationDao
    abstract val lineDao: LineDao
    abstract val pathDao: PathDao
}