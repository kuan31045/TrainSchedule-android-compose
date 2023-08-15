package com.kappstudio.trainschedule.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kappstudio.trainschedule.data.local.entity.StationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StationDao {
    @Upsert
    suspend fun upsertAll(stations: List<StationEntity>)

    @Query("SELECT * from stations WHERE id = :id")
    fun getStation(id: String): Flow<StationEntity>
}