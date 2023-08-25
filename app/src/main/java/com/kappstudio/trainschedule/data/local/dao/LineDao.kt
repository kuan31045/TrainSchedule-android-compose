package com.kappstudio.trainschedule.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kappstudio.trainschedule.data.local.entity.LineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LineDao {
    @Upsert
    suspend fun upsertAll(lines: List<LineEntity>)

    @Query("SELECT * from lines WHERE id = :id")
    fun get(id: String): LineEntity
}