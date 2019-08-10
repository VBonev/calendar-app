package com.today.calendarevents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_time_slot.*
import java.util.*
import android.content.ContentValues
import android.provider.CalendarContract
import android.widget.Toast
import android.widget.ArrayAdapter
import android.widget.AdapterView


class TimeSlotFragment : DialogFragment() {
    var currentInterval: Int = HALF_HOUR_INTERVAL

    interface OnInsertEventListener {
        fun onInsert()
    }

    var onInsertListener: OnInsertEventListener? = null

    companion object {
        fun newInstance(events: ArrayList<CalendarEvent>?): TimeSlotFragment {
            val frag = TimeSlotFragment()
            val args = Bundle()
            args.putParcelableArrayList("events", events)
            frag.arguments = args
            return frag
        }

        const val ONE_MINUTE_INTERVAL = 60 * 1000
        const val HALF_HOUR_INTERVAL = 30 * ONE_MINUTE_INTERVAL
        const val ONE_HOUR_INTERVAL = 2 * HALF_HOUR_INTERVAL
        const val TOTAL_MINUTES_VALUE = 60.0
        const val MIDDLE_RATIO = 0.5
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_time_slot, container)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val events: List<CalendarEvent>? = arguments?.getParcelableArrayList("events")
        val busyEvents = events?.filter {
            it.busy == true
        }
        busyEvents?.let {
            val startTimeSlot = getFirstTimeSlot(it)
            val endTimeSlot = startTimeSlot + currentInterval

            val intervals: Array<String> = arrayOf("30 minutes", "1 Hour")
            val adapter = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, intervals)
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            time_interval_spinner.adapter = adapter
            time_interval_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                    currentInterval = if (pos == 0) {
                        HALF_HOUR_INTERVAL
                    } else {
                        ONE_HOUR_INTERVAL
                    }
                    setTimeSlotLabel(startTimeSlot, endTimeSlot)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            setTimeSlotLabel(startTimeSlot, endTimeSlot)

            ok_button.setOnClickListener {
                if (slot_name.text.isNotEmpty()) {
                    insertEvent(
                        startTimeSlot,
                        endTimeSlot,
                        slot_name.text.toString(),
                        slot_notes.text.toString()
                    )
                    onInsertListener?.onInsert()
                    dismiss()
                } else {
                    Toast.makeText(context, "Enter event title", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setTimeSlotLabel(startTime: Long, endTime: Long) {
        time_slot_label.text = context?.getString(
            R.string.time_slot_values,
            DateUtils.getDate(startTime, "HH:mm"),
            DateUtils.getDate(endTime + currentInterval, "HH:mm")
        )
    }

    private fun insertEvent(startTime: Long, endTime: Long, title: String, note: String = "") {

        val timeSlotEvent = ContentValues()
        timeSlotEvent.put(CalendarContract.Events.CALENDAR_ID, 4)
        timeSlotEvent.put(CalendarContract.Events.TITLE, title)
        timeSlotEvent.put(CalendarContract.Events.DESCRIPTION, note)
        timeSlotEvent.put(CalendarContract.Events.EVENT_LOCATION, "Sofia")
        timeSlotEvent.put(CalendarContract.Events.DTSTART, startTime)
        timeSlotEvent.put(CalendarContract.Events.DTEND, endTime)
        timeSlotEvent.put(CalendarContract.Events.ALL_DAY, 0)
        timeSlotEvent.put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
        timeSlotEvent.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().displayName)
        activity?.contentResolver?.insert(CalendarContract.Events.CONTENT_URI, timeSlotEvent)
    }

    private fun getRoundedTime(): Calendar {
        val cal = Calendar.getInstance()

        val ratio = cal.get(Calendar.MINUTE).toDouble().div(TOTAL_MINUTES_VALUE)

        if (ratio > MIDDLE_RATIO) {
            cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) + 1)
            cal.set(Calendar.MINUTE, 0)
        } else {
            cal.set(Calendar.MINUTE, 30)
        }
        return cal
    }

    private fun getFirstTimeSlot(events: List<CalendarEvent>): Long {
        var timeSlotStart = getRoundedTime().timeInMillis
        var timeSlotEnd = timeSlotStart + currentInterval
        for (event in events) {
            val startTime = event.startDate ?: 0

            if (startTime in timeSlotStart - ONE_MINUTE_INTERVAL until timeSlotEnd + ONE_MINUTE_INTERVAL) {
                timeSlotStart = timeSlotEnd
                timeSlotEnd = timeSlotStart + currentInterval
            }
        }
        return timeSlotStart
    }
}
