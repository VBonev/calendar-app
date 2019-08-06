package com.today.calendarevents

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CalendarEvent(
    val id: String,
    var name: String? = null,
    var startDate: Long? = null,
    var endDate: Long? = null,
    var description: String? = null,
    var allDay: Boolean? = null,
    var busy: Boolean? = null,
    var calColor: String? = null,
    var location: String? = null,
    var attendees: Array<String?>? = null,
    var calendarName: String? = null
) : Parcelable

@Parcelize
data class EventAttendee(
    val id: String,
    var name: String? = null,
    var email: String? = null
) : Parcelable

