package com.kappstudio.trainschedule.domain.model

data class Train(
    val number: String,
    val name: Name = Name()
)
