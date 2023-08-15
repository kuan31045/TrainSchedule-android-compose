package com.kappstudio.trainschedule.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.kappstudio.trainschedule.data.local.entity.PathEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PathDao {

    @Upsert
    suspend fun insert(path: PathEntity)

    @Delete
    suspend fun delete(path: PathEntity)

    @Query("SELECT * from paths ORDER BY id ASC")
    fun getAllPaths(): Flow<List<PathEntity>>

    @Query("SELECT * from paths WHERE id = :id")
    fun getPath(id: String): PathEntity?
}