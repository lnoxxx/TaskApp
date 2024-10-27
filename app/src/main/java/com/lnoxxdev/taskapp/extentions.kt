package com.lnoxxdev.taskapp

import java.time.LocalDate

fun LocalDate.isLastInMonthDay(): Boolean {
    val nextDay = this.plusDays(1)
    return nextDay.dayOfMonth == 1
}
