package com.kappstudio.trainschedule.data

import com.kappstudio.trainschedule.data.remote.dto.StationDto
import com.kappstudio.trainschedule.domain.model.Name
import com.kappstudio.trainschedule.domain.model.Station

fun StationDto.toStation(): Station {
    val countyZh = stationAddress.filter { !it.isDigit() }.take(2).ifEmpty { "unknown" }
    return Station(
        id = stationId,
        name = stationName,
        county = Name(
            en = countyMap[countyZh] ?: "unknown",
            zh = countyZh
        )
    )
}

val countyMap = mapOf(
    "基隆" to "Keelung",
    "新北" to "New Taipei",
    "臺北" to "Taipei",
    "桃園" to "Taoyuan",
    "新竹" to "Hsinchu",
    "苗栗" to "Miaoli",
    "臺中" to "Taichung",
    "彰化" to "Changhua",
    "南投" to "Nantou",
    "雲林" to "Yunlin",
    "嘉義" to "Chiayi",
    "臺南" to "Tainan",
    "高雄" to "Kaohsiung",
    "屏東" to "Pingtung",
    "宜蘭" to "Yilan",
    "花蓮" to "Hualien",
    "臺東" to "Taitung"
)