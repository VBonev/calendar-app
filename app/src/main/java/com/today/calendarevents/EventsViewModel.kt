package com.today.calendarevents

import android.content.ContentResolver
import android.provider.CalendarContract
import android.util.Log
import android.util.SparseArray
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.lang.Exception


class EventsViewModel(private val contentResolver: ContentResolver) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    val calendarEvents: MutableLiveData<List<CalendarEvent>> = MutableLiveData()

    //    private fun readCalendars(): SparseArray<String> {
//        val cursor = contentResolver.query(
//            CalendarContract.Calendars.CONTENT_URI,
//            arrayOf(
//                CalendarContract.Calendars._ID,
//                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
//            ), null, null, null
//        )
//
//        // Get calendars id
//        val calendars = SparseArray<String>()
//        cursor?.let {
//            if (it.count > 0) {
//                while (it.moveToNext()) {
//                    calendars.put(it.getInt(0), it.getString(1))
//                }
//            }
//            it.close()
//        }
//        return calendars
//    }

     fun getCalendarEvents() {
        compositeDisposable += Singles.zip(readEvents(), readCalendars(), readColors()).map { dataPair ->

            val calEvents = dataPair.first
            val calendars = dataPair.second
            val colors = dataPair.third

            for (event in calEvents) {
                event.calendarName = calendars[event.calendarId ?: 0]
                event.calDisplayColor = colors[event.calColorId ?: 0]
            }
            calEvents
        }.map {

            for (i in 0 until it.size) {
                getAllAttendees(it[i])
            }
            it
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = {
                    Log.e("Error", it.message)
                }, onSuccess = {
                    calendarEvents.postValue(it)
                }
            )
    }

    private fun readCalendars(): SingleSource<SparseArray<String>> {
        return Single.create { emitter ->
            val cursor = contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                arrayOf(
                    CalendarContract.Calendars._ID,
                    CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
                ), null, null, null
            )

            // Get calendars id
            val calendars = SparseArray<String>()
            cursor.let {
                while (it.moveToNext()) {
                    calendars.put(it.getInt(0), it.getString(1))
                }
                it.close()
            }
            emitter.onSuccess(calendars)
        }
    }

    private fun readColors(): SingleSource<SparseArray<String>> {
        return Single.create { emitter ->
            val cursor = contentResolver.query(
                CalendarContract.Colors.CONTENT_URI,
                arrayOf(
                    CalendarContract.Colors.COLOR_KEY,
                    CalendarContract.Colors.COLOR
                ), null, null, null
            )

            // Get calendars id
            val colors = SparseArray<String>()
            cursor?.let {
                if (it.count > 0) {
                    while (it.moveToNext()) {
                        colors.put(it.getInt(0), it.getString(1))
                    }
                }
                it.close()
            }
            emitter.onSuccess(colors)
        }
    }

   private fun readEvents(): SingleSource<ArrayList<CalendarEvent>> {
        return Single.create { emitter ->
            try {
                val cursor = contentResolver
                    .query(
                        CalendarContract.Events.CONTENT_URI,
                        arrayOf(
                            CalendarContract.Events._ID,
                            CalendarContract.Events.TITLE,
                            CalendarContract.Events.DESCRIPTION,
                            CalendarContract.Events.DTSTART,
                            CalendarContract.Events.DTEND,
                            CalendarContract.Events.CALENDAR_COLOR_KEY,
                            CalendarContract.Events.ALL_DAY,
                            CalendarContract.Events.AVAILABILITY,
                            CalendarContract.Events.EVENT_LOCATION,
                            CalendarContract.Events.CALENDAR_ID
                        ),
                        null,
                        null,
                        CalendarContract.Events.DTSTART + " ASC"
                    )
                val calEvents = ArrayList<CalendarEvent>()
                cursor?.let { cursor ->
                    if (cursor.count > 0) {
                        while (cursor.moveToNext()) {
                            val calEvent = CalendarEvent(cursor.getInt(0))
                            calEvent.name = cursor.getString(1)
                            calEvent.description = cursor.getString(2)
                            calEvent.startDate = cursor.getString(3)?.toLong()
                            calEvent.endDate = cursor.getString(4)?.toLong()
                            calEvent.calColorId = cursor.getInt(5)
                            calEvent.allDay = cursor.getInt(6) == 1
                            calEvent.busy = (cursor.getInt(7) == CalendarContract.Events.AVAILABILITY_BUSY)
                            calEvent.location = cursor.getString(8)
                            calEvent.calendarId = cursor.getInt(9)
                            calEvents.add(calEvent)
                        }
                    }
                    cursor.close()
                }
                emitter.onSuccess(calEvents)
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    private fun getAllAttendees(event: CalendarEvent) {

        compositeDisposable += Single.create(SingleOnSubscribe<List<EventAttendee>> { emitter ->
            try {
                val cursor = contentResolver
                    .query(
                        CalendarContract.Attendees.CONTENT_URI,
                        arrayOf(
                            CalendarContract.Attendees._ID,
                            CalendarContract.Attendees.ATTENDEE_NAME,
                            CalendarContract.Attendees.ATTENDEE_EMAIL,
                            CalendarContract.Attendees.STATUS
                        ),
                        CalendarContract.Attendees.EVENT_ID + "=" + event.id,
                        null,
                        null
                    )
                val attendees = ArrayList<EventAttendee>()
                cursor?.let { it ->
                    while (it.moveToNext()) {
                        val calEvent = EventAttendee(it.getString(0))
                        calEvent.name = it.getString(1)
                        calEvent.email = it.getString(2)
                        calEvent.status = it.getInt(3)
                        attendees.add(calEvent)
                    }
                    it.close()
                }
                if (attendees.isNotEmpty()) {
                    emitter.onSuccess(attendees)
                }
            } catch (e: Exception) {
                emitter.onError(e)
            }

        }
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onSuccess = {
                    event.attendees = it
                },
                onError = {
                    Log.e("Error", it.message)
                }
            )
    }

    fun getHeaderIndexes(events: List<CalendarEvent>): ArrayList<Long> {

        val headerIndexes = ArrayList<Long>()
        for (i in 0 until events.size - 1) {
            val dayOfYear = DateUtils.getDayOfYear(events[i].startDate)
            val dayOfYear1 = DateUtils.getDayOfYear(events[i + 1].startDate)
            if (dayOfYear != dayOfYear1) {
                headerIndexes.add(i + 1.toLong())
            } else {
                headerIndexes.add(i.toLong())
            }
        }
        return headerIndexes

    }
}



