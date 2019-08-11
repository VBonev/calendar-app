package com.today.calendarevents.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.today.calendarevents.model.CalendarEvent
import com.today.calendarevents.R
import com.today.calendarevents.Utils
import kotlinx.android.synthetic.main.fragment_event_details.*


class EventDetailsFragment : DialogFragment() {

    companion object {
        private const val EVENT_KEY = "event"

        fun newInstance(eventDetails: CalendarEvent): EventDetailsFragment {
            val frag = EventDetailsFragment()
            val args = Bundle()
            args.putParcelable(EVENT_KEY, eventDetails)
            frag.arguments = args
            return frag
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event_details, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val eventDetails: CalendarEvent? = arguments?.getParcelable(EVENT_KEY)

        eventDetails?.let {
            event_name.text = it.name
            calendar_name.text = it.calendarName
            start_time.text = Utils.getDate(it.startDate, "hh:mm   dd-MM")
            end_time.text = Utils.getDate(it.endDate, "hh:mm   dd-MM")

            if (it.location?.isNotEmpty() == true) {
                location.text = it.location
            } else {
                location_layout.visibility = View.GONE
            }

            if (it.notes?.isNotEmpty() == true) {
                location.text = it.notes
            } else {
                notes_layout.visibility = View.GONE
            }

            if (it.attendees?.isNotEmpty() == true) {
                attendees_container.visibility = View.VISIBLE
                event_attendees.layoutManager = LinearLayoutManager(context)
                event_attendees.adapter = it.attendees?.let { EventAttendeesAdapter(it) }
            }
            event_name.setBackgroundColor(Utils.getDisplayColor(it.calDisplayColor))
        }
    }

}