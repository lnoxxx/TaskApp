package com.lnoxxdev.data

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DateTimeManager {
    fun getNowDate(): LocalDate {
        return LocalDate.now()
    }

    fun getNowTime(): LocalTime {
        return LocalTime.now()
    }

    fun getDateFormatter(): DateTimeFormatter {
        return DateTimeFormatter.ofPattern("dd.MM.yyyy")
    }

    fun getTimeFormatter(): DateTimeFormatter {
        return DateTimeFormatter.ofPattern("HH:mm")
    }

    fun getDateEpochMilli(date: LocalDate): Long {
        return date.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
    }

    fun getDateFromEpochMilli(epochMilli: Long): LocalDate {
        val instant = Instant.ofEpochMilli(epochMilli)
        return instant.atZone(ZoneId.of("UTC")).toLocalDate()
    }
}