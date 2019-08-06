package com.today.calendarevents

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun getDate(milliSeconds: Long?, pattern: String): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        val calendar = Calendar.getInstance()
        if (milliSeconds != null) {
            calendar.timeInMillis = milliSeconds
        }
        return formatter.format(calendar.time)
    }

    fun getDayOfYear(milliSeconds: Long?): Int {
        val calendar = Calendar.getInstance()
        if (milliSeconds != null) {
            calendar.timeInMillis = milliSeconds
        }
        return calendar.get(Calendar.DAY_OF_YEAR)
    }
}