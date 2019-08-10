package com.today.calendarevents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_event_details.*


class EventDetailsFragment : DialogFragment() {
    companion object {
        fun newInstance(eventDetails: CalendarEvent): EventDetailsFragment {
            val frag = EventDetailsFragment()
            val args = Bundle()
            args.putParcelable("event", eventDetails)
            frag.arguments = args
            return frag
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val eventDetails: CalendarEvent? = arguments?.getParcelable("event")

        eventDetails?.let { eventDetails ->
            event_name.text = eventDetails.name
            start_time.text = DateUtils.getDate(eventDetails.startDate, "hh:mm   dd-MM")
            end_time.text = DateUtils.getDate(eventDetails.endDate, "hh:mm   dd-MM")
            location.text = eventDetails.location
            calendar_name.text = eventDetails.calendarName
            notes.text = eventDetails.description
            if (eventDetails.attendees?.isNotEmpty() == true) {
                attendees_container.visibility = View.VISIBLE
                event_attendees.layoutManager = LinearLayoutManager(context)
                event_attendees.adapter = eventDetails.attendees?.let { EventAttendeesAdapter(it) }
            }
        }
        ok_button.setOnClickListener { dismiss() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event_details, container)
    }

}