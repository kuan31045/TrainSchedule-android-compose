package com.kappstudio.trainschedule.util

fun <T> List<T>.toggle(item: T): List<T> {
    return if (item in this) {
        this.minus(item)
    } else {
        this.plus(item)
    }
}