package com.today.calendarevents

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CalendarEvent(
    val id: Int,
    var name: String? = null,
    var startDate: Long? = null,
    var endDate: Long? = null,
    var description: String? = null,
    var allDay: Boolean? = null,
    var busy: Boolean? = null,
    var calColorId: Int? = null,
    var calDisplayColor: String? = null,
    var location: String? = null,
    var attendees: List<EventAttendee>? = null,
    var calendarName: String? = null,
    var calendarId: Int? = null
) : Parcelable

@Parcelize
data class EventAttendee(
    val id: String,
    var name: String? = null,
    var email: String? = null,
    var status: Int? = null
) : Parcelable


