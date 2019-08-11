package com.today.calendarevents.data

import android.os.Parcelable
import android.provider.CalendarContract
import com.today.calendarevents.R
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventAttendee(
    val id: String,
    var name: String? = null,
    var email: String? = null
) : Parcelable {
    var status: Int = R.string.attendee_status_none
        get() {
            return when (field) {
                CalendarContract.Attendees.ATTENDEE_STATUS_ACCEPTED -> R.string.attendee_status_accepted
                CalendarContract.Attendees.ATTENDEE_STATUS_DECLINED -> R.string.attendee_status_declined
                CalendarContract.Attendees.ATTENDEE_STATUS_INVITED -> R.string.attendee_status_invited
                else -> R.string.attendee_status_none
            }
        }
}

