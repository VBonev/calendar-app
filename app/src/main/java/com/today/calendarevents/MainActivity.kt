package com.today.calendarevents

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object COMPANION {
        const val MY_PERMISSIONS_REQUEST_READ_CALENDAR = 2
    }

    private fun getAllCalendars(): Array<String?> {

        val cursor = contentResolver.query(
            Uri.parse("content://com.android.calendar/calendars"),
            arrayOf("_id", "calendar_displayName", "calendar_color"), null, null, null
        )

        cursor.moveToFirst()
        val calendarNames = arrayOfNulls<String>(cursor.count)
        val calendarColors = arrayOfNulls<String>(cursor.count)
        // Get calendars id
        val calendarIds = arrayOfNulls<Int>(cursor.count)
        for (i in 0 until cursor.count) {
            calendarIds[i] = cursor.getInt(0)
            calendarNames[i] = cursor.getString(1)
            calendarColors[i] = cursor.getString(2)
            Log.i(
                "@calendar",
                "Calendar Name : " + calendarNames[i] + " Calendar color : " + calendarColors[i] + " Calendar id : " + calendarIds[i]
            )
            cursor.moveToNext()
        }
        return calendarNames
    }

    private fun readCalendarEvent(): ArrayList<CalendarEvent> {

        val cursor = contentResolver
            .query(
                CalendarContract.Events.CONTENT_URI,
                arrayOf(
                    "calendar_id",
                    "title",
                    "description",
                    "dtstart",
                    "dtend",
                    "displayColor",
                    "allDay",
                    "availability",
                    "eventLocation"
                ),
                null,
                null,
                null
            )
        val calEvents = ArrayList<CalendarEvent>(cursor.count)
        cursor?.let { it ->
            it.moveToFirst()
            for (i in 0 until cursor.count) {
                val calEvent = CalendarEvent(it.getString(1))
                calEvent.description = it.getString(2)
                calEvent.startDate = it.getString(3)
                calEvent.endDate = it.getString(4)
                calEvent.allDay = it.getString(5)
                calEvent.busy = it.getString(6)
                calEvent.calColor = it.getString(7)
                calEvent.location = it.getString(8)
                calEvents.add(calEvent)
                it.moveToNext()

            }
        }
        return calEvents
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
        val objects = readCalendarEvent()
        val calendars = getAllCalendars()
        val itemsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, objects)
        val calendarEvents: RecyclerView = calendar_events as RecyclerView
//        calendarEvents.adapter=itemsAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions()
    }
}
