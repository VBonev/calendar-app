package com.today.calendarevents.details

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.today.calendarevents.base.BaseViewModel
import com.today.calendarevents.data.CalendarEvent

class EventDetailsViewModel(app: Application) : BaseViewModel(app){
    val event = MutableLiveData<CalendarEvent>()
}