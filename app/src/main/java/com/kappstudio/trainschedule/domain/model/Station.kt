package com.kappstudio.trainschedule.domain.model

import com.kappstudio.trainschedule.util.localize

data class Station(
    val id: String = "",
    val name: Name = Name(),
    val county: Name = Name()
){
    val localName = name.localize()
}