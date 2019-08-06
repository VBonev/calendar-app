package com.today.calendarevents

data class CalendarEvent(
    val name: String,
    var startDate: String? = null,
    var endDate: String? = null,
    var description: String? = null,
    var allDay: String? = null,
    var busy: String? = null,
    var calColor: String? = null,
    var location: String? = null)

