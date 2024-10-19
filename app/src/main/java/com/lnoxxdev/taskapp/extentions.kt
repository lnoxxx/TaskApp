package com.lnoxxdev.taskapp

import android.content.res.Resources
import java.time.LocalDate

fun LocalDate.isLastInMonthDay(): Boolean {
    val nextDay = this.plusDays(1)
    return nextDay.dayOfMonth == 1
}

fun Float.dpToPx(): Int {
    val density = Resources.getSystem().displayMetrics.density
    return (this * density).toInt()
}
