package com.today.calendarevents

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(){
    private var stickyHeaderDecorator: StickyHeaderDecoration? = null
    private val eventsViewModel by lazy { EventsViewModel(contentResolver) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions()
    }

    companion object COMPANION {
        const val MODIFY_CALENDAR_PERMISSIONS_REQUEST = 2
    }


    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.READ_CALENDAR
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR),
                    MODIFY_CALENDAR_PERMISSIONS_REQUEST
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MODIFY_CALENDAR_PERMISSIONS_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    setUpList()
                }
                return
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.events_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_time_slot -> {

            val timeSlotFragment = TimeSlotFragment.newInstance(eventsViewModel.calendarEvents.value as ArrayList<CalendarEvent>?)

            timeSlotFragment.onInsertListener = object: TimeSlotFragment.OnInsertEventListener{
                override fun onInsert() {
                    setUpList()
                }
            }

            timeSlotFragment.show(supportFragmentManager, "time_slot")
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun setUpList() {
        eventsViewModel.getCalendarEvents()
        eventsViewModel.calendarEvents.observe(this, Observer { it ->
            it?.let { it ->
                val headerIndexes = eventsViewModel.getHeaderIndexes(it)
                val eventsAdapter = EventsAdapter(it, headerIndexes)
                eventsAdapter.setItemAction {
                    val editNameDialogFragment = EventDetailsFragment.newInstance(it)
                    editNameDialogFragment.show(supportFragmentManager, "event_details")
                }
                calendar_events.layoutManager = LinearLayoutManager(this)
                if (stickyHeaderDecorator != null) {
                    calendar_events.removeItemDecoration(stickyHeaderDecorator!!)
                }
                stickyHeaderDecorator = StickyHeaderDecoration(eventsAdapter)
                calendar_events.addItemDecoration(stickyHeaderDecorator!!)
                calendar_events.invalidateItemDecorations()
                calendar_events.adapter = eventsAdapter
            }
        })

    }

}
