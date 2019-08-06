package com.today.calendarevents

import java.text.SimpleDateFormat
import java.util.*

data class CalendarEvent(val id: String) {

    var name: String? = null
    var startDate: String? = null
        get() = getDate(startDate?.toLong())
        set(value) {
            if (value != null)
                field = value
        }
    var endDate: String? = null
        get() = getDate(endDate?.toLong())
        set(value) {
            if (value != null)
                field = value
        }

    var description: String? = null
    var allDay: Boolean? = null
    var busy: Boolean? = null
    var calColor: String? = null
    var location: String? = null
    var attendees: List<EventAttendee>? = null
    private fun getDate(milliSeconds: Long?): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault())
        val calendar = Calendar.getInstance()
        if (milliSeconds != null) {
            calendar.timeInMillis = milliSeconds
        }
        return formatter.format(calendar.time)
    }

    data class EventAttendee(val id: String) {

        var name: String? = null
        var email: String? = null
    }
}
