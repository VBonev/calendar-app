package com.today.calendarevents

import android.graphics.Color
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

object Utils {

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

    fun getDisplayColor(color: Int): Int {
        val fArr = FloatArray(3)
        Color.colorToHSV(color, fArr)
        if (fArr[2] > 0.79f) {
            fArr[1] = min(fArr[1] * 1.3f, 1.0f)
            fArr[2] = fArr[2] * 0.8f
        }
        return Color.HSVToColor(Color.alpha(color), fArr)
    }

    fun getCurrentTime( pattern: String): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        val calendar = Calendar.getInstance()
        return formatter.format(calendar.time)
    }
}