package com.lnoxxdev.data.dateRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

data class DateRange(
    val dateList: List<LocalDate>,
    val selectedDate: LocalDate?,
)

class DateRepository {

    private val timeStock = 15L
    private val defaultValue = DateRange(getInitialDateRange(), null)

    private val _dateState = MutableStateFlow(defaultValue)
    val dateState: Flow<DateRange> = _dateState.asStateFlow()

    private fun getInitialDateRange(): List<LocalDate> {
        val nowDate = LocalDate.now()
        return createDateList(nowDate.minusDays(timeStock), nowDate.plusDays(timeStock))
    }

    fun extendDateFuture() {
        val oldDateList = _dateState.value.dateList
        val newLastDay = oldDateList.last().plusDays(timeStock)
        val newFirstDay = oldDateList.first()
        val newDateList = createDateList(newFirstDay, newLastDay)
        _dateState.value = DateRange(dateList = newDateList, selectedDate = null)
    }

    fun extendDatePast() {
        val oldDateList = _dateState.value.dateList
        val newFirstDay = oldDateList.first().minusDays(timeStock)
        val newLastDay = oldDateList.last()
        val newDateList = createDateList(newFirstDay, newLastDay)
        _dateState.value = DateRange(dateList = newDateList, selectedDate = null)
    }

    private fun createDateList(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
        val dateList = mutableListOf<LocalDate>()
        var currentDate = startDate
        dateList.add(currentDate)
        while (!currentDate.isEqual(endDate)) {
            currentDate = currentDate.plusDays(1L)
            dateList.add(currentDate)
        }
        return dateList
    }

    fun selectDate(date: LocalDate) {
        val oldState = _dateState.value
        val oldFirstDate = oldState.dateList.first()
        val oldLastDay = oldState.dateList.last()
        if (date.isBefore(oldFirstDate)) {
            val newFirstDate = date.minusDays(timeStock)
            val newDateList = createDateList(newFirstDate, oldLastDay)
            _dateState.value = DateRange(newDateList, date)
        } else if (date.isAfter(oldLastDay)) {
            val newLastDate = date.plusDays(timeStock)
            val newDateList = createDateList(oldFirstDate, newLastDate)
            _dateState.value = DateRange(newDateList, date)
        } else {
            _dateState.value = oldState.copy(selectedDate = date)
        }
    }

    fun dateSelected() {
        _dateState.value = _dateState.value.copy(selectedDate = null)
    }
}