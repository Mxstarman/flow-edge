package com.ecrisar.flow.edge.model

import java.time.LocalDate

/**
 * Model representing a calendar event in the Calendar Edge Panel.
 */
data class CalendarEvent(
    val id: String,
    val title: String,
    val time: String,
    val date: LocalDate,
    val colorHex: Long = 0xFF0381FE
)
