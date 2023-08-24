package com.kappstudio.trainschedule.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kappstudio.trainschedule.data.local.entity.StationEntity

class Converters {
    @TypeConverter
    fun convertListToJson(list: List<StationEntity>?): String? {
        list?.let {
            return Gson().toJson(list)
        }
        return null
    }

    @TypeConverter
    fun convertJsonToStationEntities(json: String?): List<StationEntity>? {
        return Gson().fromJson(json, object : TypeToken<List<StationEntity>>() {}.type)
    }
}