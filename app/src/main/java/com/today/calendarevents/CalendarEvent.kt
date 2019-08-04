package com.today.calendarevents

import java.text.SimpleDateFormat
import java.util.*

data class CalendarEvent( val name: String){

    var startDate: String? = null
        get() = getDate(startDate?.toLong())
        set(value) {
            if(value !=null)
                field=value
        }
    var endDate: String? = null
        get() = getDate(endDate?.toLong())
        set(value) {
            if(value !=null)
                field=value
        }

    var description: String?=null
    var allDay:String?=null
    var busy:String?=null
    var calColor:String?=null
    var location:String?=null

    private fun getDate(milliSeconds: Long?): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a",Locale.getDefault())
        val calendar = Calendar.getInstance()
        if (milliSeconds != null) {
            calendar.timeInMillis = milliSeconds
        }
        return formatter.format(calendar.time)
    }
}
