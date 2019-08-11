package com.today.calendarevents.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventAttendee(
    val id: String,
    var name: String? = null,
    var email: String? = null,
    var status: Int? = null
) : Parcelable

