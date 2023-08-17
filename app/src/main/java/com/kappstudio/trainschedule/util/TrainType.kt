package com.kappstudio.trainschedule.util

import androidx.annotation.StringRes
import com.kappstudio.trainschedule.R

enum class TrainType(
    val typeCode: Int,
    @StringRes val trainName: Int,
) {
    TC(typeCode = 3, trainName = R.string.tc_express),
    NEW_TC(typeCode = 11, trainName = R.string.new_tc_express),
    TAROKO(typeCode = 1, trainName = R.string.taroko_express),
    PUYUMA(typeCode = 2, trainName = R.string.puyuma_express),
    CK(typeCode = 4, trainName = R.string.ck_express),
    LOCAL(typeCode = 6, trainName = R.string.local_train),
    FAST_LOCAL(typeCode = 10, trainName = R.string.fast_local_train);

    companion object {
        fun getTypes(mainType: Int): List<TrainType> {
            return when (mainType) {
                0 -> enumValues<TrainType>().toList()
                1 -> enumValues<TrainType>().take(5).toList()
                else -> enumValues<TrainType>().takeLast(2).toList()
            }
        }

        fun getName(typeCode: Int): Int? =
            TrainType.values().firstOrNull { it.typeCode == typeCode }?.trainName
    }
}