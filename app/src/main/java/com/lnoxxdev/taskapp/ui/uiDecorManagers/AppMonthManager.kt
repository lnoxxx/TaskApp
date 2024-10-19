package com.lnoxxdev.taskapp.ui.uiDecorManagers

import com.lnoxxdev.taskapp.R

object AppMonthManager {

    fun getImageOfMonth(month: Int): Int {
        return when (month) {
            1 -> R.drawable.january
            2 -> R.drawable.february
            3 -> R.drawable.march
            4 -> R.drawable.april
            5 -> R.drawable.may
            6 -> R.drawable.june
            7 -> R.drawable.july
            8 -> R.drawable.august
            9 -> R.drawable.september
            10 -> R.drawable.october
            11 -> R.drawable.november
            12 -> R.drawable.december
            else -> R.drawable.img
        }
    }

    fun getNameOfMonth(month: Int): Int {
        return when (month) {
            1 -> R.string.january
            2 -> R.string.february
            3 -> R.string.march
            4 -> R.string.april
            5 -> R.string.may
            6 -> R.string.june
            7 -> R.string.july
            8 -> R.string.august
            9 -> R.string.september
            10 -> R.string.october
            11 -> R.string.november
            12 -> R.string.december
            else -> R.string.unknown_month
        }
    }
}