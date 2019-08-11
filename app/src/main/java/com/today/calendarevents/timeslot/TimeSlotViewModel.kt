package com.today.calendarevents.timeslot

import android.app.Application
import android.content.ContentValues
import android.provider.CalendarContract
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.today.calendarevents.base.BaseViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.*

class TimeSlotViewModel(app: Application) : BaseViewModel(app) {
    val inserted: MutableLiveData<Boolean> = MutableLiveData()

    fun insertEvent(
        title: String,
        notes: String,
        startTime: Long,
        endTime: Long
    ) {
        compositeDisposable += Completable.create { emitter ->
            try {
                val timeSlotEvent = ContentValues()
                timeSlotEvent.put(CalendarContract.Events.CALENDAR_ID, 4)
                timeSlotEvent.put(CalendarContract.Events.TITLE, title)
                timeSlotEvent.put(CalendarContract.Events.DESCRIPTION, notes)
                timeSlotEvent.put(CalendarContract.Events.DTSTART, startTime)
                timeSlotEvent.put(CalendarContract.Events.DTEND, endTime)
                timeSlotEvent.put(CalendarContract.Events.ALL_DAY, 0)
                timeSlotEvent.put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                timeSlotEvent.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().displayName)
                contentResolver.insert(CalendarContract.Events.CONTENT_URI, timeSlotEvent)
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onComplete = {
                    inserted.postValue(true)
                },
                onError = {
                    Log.e("Error", it.message)
                    inserted.postValue(false)
                }
            )

    }
}