package com.lnoxxdev.taskapp.ui.uiDecorManagers

import com.lnoxxdev.taskapp.R

object AppWeekManager {
    fun getWeekDayName(weekDay: Int): Int{
        return when(weekDay){
            1 -> R.string.week_day_1_monday
            2-> R.string.week_day_2_tuesday
            3 -> R.string.week_day_3_wednesday
            4 -> R.string.week_day_4_thursday
            5 -> R.string.week_day_5_friday
            6 -> R.string.week_day_6_saturday
            7 -> R.string.week_day_7_sunday
            else -> R.string.unknown_week
        }
    }
}