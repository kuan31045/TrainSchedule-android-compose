package com.kappstudio.trainschedule.util

import java.time.format.DateTimeFormatter

val dateFormatter = DateTimeFormatter.ofPattern("MM/dd EEE")
val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
val detailFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
