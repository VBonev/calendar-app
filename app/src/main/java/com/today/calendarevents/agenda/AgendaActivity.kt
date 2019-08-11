package com.today.calendarevents.agenda

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration
import com.today.calendarevents.*
import com.today.calendarevents.data.CalendarEvent
import com.today.calendarevents.details.EventDetailsFragment
import com.today.calendarevents.timeslot.TimeSlotFragment
import kotlinx.android.synthetic.main.activity_agenda.*


class AgendaActivity : AppCompatActivity() {
    private var stickyHeaderDecorator: StickyHeaderDecoration? = null
    private val viewModel by lazy { AgendaViewModel(contentResolver) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)

        viewModel.isLoading.observe(this, Observer { showOrHideProgressScreen(it) })
        checkPermissions()
    }

    companion object COMPANION {
        const val MODIFY_CALENDAR_PERMISSIONS_REQUEST = 2
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this@AgendaActivity,
                Manifest.permission.READ_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@AgendaActivity,
                arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR),
                MODIFY_CALENDAR_PERMISSIONS_REQUEST
            )
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

            val timeSlotFragment =
                TimeSlotFragment.newInstance(viewModel.calendarEvents.value as ArrayList<CalendarEvent>?)
            timeSlotFragment.onInsertListener = object :
                TimeSlotFragment.OnInsertEventListener {
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
        viewModel.getCalendarEvents()
        viewModel.calendarEvents.observe(this, Observer { events ->
            val headerIndexes = viewModel.getHeaderIndexes(events)
            val eventsAdapter = AgendaAdapter(events, headerIndexes)

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

        })

    }

    private fun showOrHideProgressScreen(flag: Boolean) {
        progress_circular?.apply {
            visibility = if (flag) View.VISIBLE else View.GONE
        }
    }
}
