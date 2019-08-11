package com.today.calendarevents.timeslot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_time_slot.*
import java.util.*
import androidx.lifecycle.Observer
import com.today.calendarevents.data.CalendarEvent
import com.today.calendarevents.R
import com.today.calendarevents.Utils


class TimeSlotFragment : DialogFragment() {

    private val viewModel by lazy { activity?.let { TimeSlotViewModel(it.contentResolver) } }
    var currentInterval: Int = HALF_HOUR_INTERVAL
    var startTimeSlot: Long = 0
    var onInsertListener: OnInsertEventListener? = null

    companion object {
        fun newInstance(events: ArrayList<CalendarEvent>?): TimeSlotFragment {
            val frag = TimeSlotFragment()
            val args = Bundle()
            args.putParcelableArrayList(EVENTS_KEY, events)
            frag.arguments = args
            return frag
        }

        private const val ONE_MINUTE_INTERVAL = 60 * 1000
        private const val HALF_HOUR_INTERVAL = 30 * ONE_MINUTE_INTERVAL
        private const val ONE_HOUR_INTERVAL = 60 * ONE_MINUTE_INTERVAL
        private const val TOTAL_MINUTES_VALUE = 60.0
        private const val MIDDLE_RATIO = 0.5
        private const val EVENTS_KEY = "events"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_time_slot, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val events: List<CalendarEvent>? = arguments?.getParcelableArrayList(EVENTS_KEY)
        viewModel?.inserted?.observe(viewLifecycleOwner, Observer { insertedEvent(it) })

        val busyEvents = events?.filter {
            it.busy == true
        }
        busyEvents?.let {

            startTimeSlot = getFirstTimeSlot(it)
            val intervals: Array<String> = arrayOf(
                context?.resources?.getString(R.string.half_an_hour_time_slot)!!,
                context?.resources?.getString(R.string.one_hour_time_slot)!!
            )
            val adapter = ArrayAdapter(
                context,
                R.layout.support_simple_spinner_dropdown_item, intervals
            )
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            time_interval_spinner.adapter = adapter
            time_interval_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                    currentInterval = if (pos == 0) {
                        HALF_HOUR_INTERVAL
                    } else {
                        ONE_HOUR_INTERVAL
                    }
                    setTimeSlotLabel(startTimeSlot, startTimeSlot + currentInterval)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            current_time.text = Utils.getCurrentTime("HH:mm")
            setTimeSlotLabel(startTimeSlot, startTimeSlot + currentInterval)

            ok_button.setOnClickListener {
                if (slot_name.text.isNotEmpty()) {
                    viewModel?.insertEvent(
                        slot_name.text.toString(),
                        slot_notes.text.toString(),
                        startTimeSlot,
                        startTimeSlot + currentInterval
                    )
                } else {
                    Toast.makeText(
                        context,
                        context?.resources?.getString(R.string.empty_event_name_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun setTimeSlotLabel(startTime: Long, endTime: Long) {
        time_slot_label.text = context?.getString(
            R.string.time_slot_values,
            Utils.getDate(startTime, "HH:mm"),
            Utils.getDate(endTime, "HH:mm")
        )
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

    private fun insertedEvent(success: Boolean) {
        if (success) {
            onInsertListener?.onInsert()
        } else {
            Toast.makeText(context, context?.resources?.getString(R.string.insert_event_error), Toast.LENGTH_LONG)
                .show()
        }
        dismiss()
    }

    interface OnInsertEventListener {
        fun onInsert()
    }
}