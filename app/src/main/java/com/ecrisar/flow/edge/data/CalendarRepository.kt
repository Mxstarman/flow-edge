package com.ecrisar.flow.edge.data

import com.ecrisar.flow.edge.model.CalendarEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.util.UUID

class CalendarRepository {
    private val today = LocalDate.now()

    private val _events = MutableStateFlow<List<CalendarEvent>>(
        listOf(
            CalendarEvent(
                id = UUID.randomUUID().toString(),
                title = "Team Standup",
                time = "10:00 AM - 10:30 AM",
                date = today,
                colorHex = 0xFF0381FE
            ),
            CalendarEvent(
                id = UUID.randomUUID().toString(),
                title = "Design Review: Edge Multitasking",
                time = "02:00 PM - 03:00 PM",
                date = today,
                colorHex = 0xFF7C4DFF
            ),
            CalendarEvent(
                id = UUID.randomUUID().toString(),
                title = "Sprint Planning",
                time = "11:00 AM - 12:00 PM",
                date = today.plusDays(1),
                colorHex = 0xFF00B0FF
            ),
            CalendarEvent(
                id = UUID.randomUUID().toString(),
                title = "Product Launch Demo",
                time = "04:00 PM - 05:30 PM",
                date = today.plusDays(3),
                colorHex = 0xFFFF4081
            )
        )
    )
    val events: StateFlow<List<CalendarEvent>> = _events.asStateFlow()

    fun getEventsForDate(date: LocalDate): List<CalendarEvent> {
        return _events.value.filter { it.date == date }
    }

    fun addEvent(title: String, time: String, date: LocalDate, colorHex: Long = 0xFF0381FE) {
        val newEvent = CalendarEvent(
            id = UUID.randomUUID().toString(),
            title = title,
            time = time,
            date = date,
            colorHex = colorHex
        )
        _events.value = _events.value + newEvent
    }

    fun deleteEvent(id: String) {
        _events.value = _events.value.filterNot { it.id == id }
    }
}
