package com.kappstudio.trainschedule.util

import androidx.annotation.StringRes
import com.kappstudio.trainschedule.R

enum class TrainType(
    val typeCode: Int,
    @StringRes val trainName: Int,
    val zhName:String
) {
    TC(typeCode = 3, trainName = R.string.tc_express, zhName = "自強"),
    NEW_TC(typeCode = 11, trainName = R.string.new_tc_express, zhName = "自強()"),
    TAROKO(typeCode = 1, trainName = R.string.taroko_express, zhName = "太魯閣"),
    PUYUMA(typeCode = 2, trainName = R.string.puyuma_express, zhName = "普悠瑪"),
    CK(typeCode = 4, trainName = R.string.ck_express, zhName = "莒光"),
    LOCAL(typeCode = 6, trainName = R.string.local_train, zhName = "區間"),
    FAST_LOCAL(typeCode = 10, trainName = R.string.fast_local_train, zhName = "區間快"),
    UNKNOWN(typeCode = 0, trainName = R.string.train, zhName = "");

    companion object {
        fun getTypes(mainType: Int): List<TrainType> {
            return when (mainType) {
                0 -> enumValues<TrainType>().toList()
                1 -> enumValues<TrainType>().take(5).toList()
                else -> enumValues<TrainType>().takeLast(3).toList()
            }
        }

        fun fromCode(typeCode: Int): TrainType =
            TrainType.values().firstOrNull { it.typeCode == typeCode } ?: UNKNOWN

       fun fromZh(zhName:String): TrainType =
            TrainType.values().firstOrNull { it.zhName == zhName } ?: UNKNOWN
    }
}