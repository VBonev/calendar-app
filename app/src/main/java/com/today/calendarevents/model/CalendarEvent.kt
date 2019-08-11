package com.today.calendarevents.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CalendarEvent(
    val id: Int,
    var name: String? = null,
    var startDate: Long? = null,
    var endDate: Long? = null,
    var notes: String? = null,
    var allDay: Boolean? = null,
    var busy: Boolean? = null,
    var calDisplayColor: Int = 0,
    var location: String? = null,
    var attendees: List<EventAttendee>? = null,
    var calendarName: String? = null,
    var calendarId: Int? = null
) : Parcelable

