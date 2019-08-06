package com.today.calendarevents

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object COMPANION {
        const val MY_PERMISSIONS_REQUEST_READ_CALENDAR = 2
    }

    private fun getAllCalendars(): HashMap<String, String> {

        val cursor = contentResolver.query(
            Uri.parse("content://com.android.calendar/calendars"),
            arrayOf("_id", "calendar_displayName", "calendar_color"), null, null, null
        )

        // Get calendars id
        val calendars = HashMap<String, String>(cursor.count)
        while (cursor.moveToNext()) {
            calendars[cursor.getString(0)] = cursor.getString(1)
        }
        cursor.close()
        return calendars
    }

    private fun readCalendarEvent(): ArrayList<CalendarEvent> {

        val cursor = contentResolver
            .query(
                CalendarContract.Events.CONTENT_URI,
                arrayOf(
                    "_id",
                    "title",
                    "description",
                    "dtstart",
                    "dtend",
                    "displayColor",
                    "allDay",
                    "availability",
                    "eventLocation",
                    "calendar_id"
                ),
                null,
                null,
                CalendarContract.Events.DTSTART + " ASC"
            )
        val calendars = getAllCalendars()
        val calEvents = ArrayList<CalendarEvent>(cursor.count)
        cursor?.let { cursor ->
            cursor.moveToFirst()
            while (cursor.moveToNext()) {
                val calEvent = CalendarEvent(cursor.getString(0))
                calEvent.name = cursor.getString(1)
                calEvent.calendarName = calendars[cursor.getString(9)]
                calEvent.attendees = getAllAttendees(calEvent.id)
                calEvent.description = cursor.getString(2)
                calEvent.startDate = cursor.getString(3)?.toLong()
                calEvent.endDate = cursor.getString(4)?.toLong()
                calEvent.calColor = cursor.getString(5)
                calEvent.allDay = cursor.getInt(6) == 0
                calEvent.busy = (cursor.getInt(7) == CalendarContract.Events.AVAILABILITY_BUSY)
                calEvent.location = cursor.getString(8)
                calEvent.attendees = getAllAttendees(calEvent.id)
                calEvents.add(calEvent)
            }
            cursor.close()
        }
        return calEvents
    }

//    private fun getAllAttendees(eventId: String): ArrayList<EventAttendee> {
//        val cursor = contentResolver
//            .query(
//                CalendarContract.Attendees.CONTENT_URI,
//                arrayOf(
//                    "_id",
//                    "attendeeName",
//                    "attendeeEmail"
//                ),
//                eventId,
//                null,
//                null
//            )
//        val attendees = ArrayList<EventAttendee>(cursor.count)
//        cursor?.let { it ->
//            it.moveToFirst()
//            for (i in 0 until cursor.count) {
//                val calEvent = EventAttendee(it.getString(0))
//                calEvent.name = it.getString(1)
//                calEvent.email = it.getString(2)
//                attendees.add(calEvent)
//                it.moveToNext()
//
//            }
//        }
//        return attendees
//    }

    private fun getAllAttendees(eventId: String): Array<String?> {
        val cursor = contentResolver
            .query(
                CalendarContract.Attendees.CONTENT_URI,
                arrayOf(
                    "_id",
                    "attendeeName",
                    "attendeeEmail"
                ),
                eventId,
                null,
                null
            )
        val attendees = arrayOfNulls<String>(cursor.count)
        cursor?.let { it ->
            it.moveToFirst()
            for (i in 0 until cursor.count) {
                attendees[i] = (it.getString(2))
                it.moveToNext()
            }
            cursor.close()
        }
        return attendees
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.READ_CONTACTS
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this@MainActivity, arrayOf(Manifest.permission.READ_CALENDAR),
                    MY_PERMISSIONS_REQUEST_READ_CALENDAR
                )

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            setUpList()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CALENDAR -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    setUpList()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

        }
    }

    private fun setUpList() {
        val events = readCalendarEvent()

        val headerIndexes = ArrayList<Long>()
        for (i in 0 until events.size-1) {
            val dayOfYear = DateUtils.getDayOfYear(events[i].startDate)
            val dayOfYear1 = DateUtils.getDayOfYear(events[i + 1].startDate)
            if (dayOfYear != dayOfYear1) {
               headerIndexes.add(i.toLong())
            }
//            else{
//                headerIndexes.add(StickyHeaderDecoration.NO_HEADER_ID)
//            }
        }

        val eventsAdapter = EventsAdapter(events, headerIndexes)
        eventsAdapter.setItemAction {
            val editNameDialogFragment = EventDetailsFragment.newInstance(it)
            editNameDialogFragment.show(supportFragmentManager, "event_details")
        }
        calendar_events.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val decor = StickyHeaderDecoration(eventsAdapter)
        calendar_events.addItemDecoration(decor)
        calendar_events.invalidateItemDecorations()
        calendar_events.adapter = eventsAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions()
    }
}
