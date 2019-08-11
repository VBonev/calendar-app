package com.today.calendarevents.details

import android.os.Bundle
import android.view.View
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.LinearLayoutManager
import com.today.calendarevents.data.CalendarEvent
import com.today.calendarevents.R
import com.today.calendarevents.Utils
import com.today.calendarevents.base.BaseDialogFragment
import com.today.calendarevents.databinding.FragmentEventDetailsBinding
import kotlinx.android.synthetic.main.fragment_event_details.*

class EventDetailsFragment : BaseDialogFragment<FragmentEventDetailsBinding, EventDetailsViewModel>() {

    override fun getViewModelResId(): Int = BR.eventDetailsVM

    override fun getLayoutResId(): Int = R.layout.fragment_event_details

    override fun getViewModelClass(): Class<EventDetailsViewModel> = EventDetailsViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {

            val event= EventDetailsFragmentArgs.fromBundle(it).event
            viewModel.event.value = event

            start_time.text = Utils.getDate(event.startDate, Utils.DATE_PATTERN)
            end_time.text = Utils.getDate(event.endDate, Utils.DATE_PATTERN)

            if (event.attendees?.isNotEmpty() == true) {
                binding.eventAttendees.layoutManager = LinearLayoutManager(this@EventDetailsFragment.context)
                attendees_container.visibility = View.VISIBLE
                binding.eventAttendees.adapter =  EventAttendeesAdapter(event.attendees)
            }
            event_name.setBackgroundColor(Utils.getDisplayColor(event.calDisplayColor))
        }
    }

}
