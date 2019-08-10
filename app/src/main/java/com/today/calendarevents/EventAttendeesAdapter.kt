package com.today.calendarevents

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventAttendeesAdapter(private val attendees: List<EventAttendee>) :
    RecyclerView.Adapter<EventAttendeesAdapter.AttendeeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendeeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return AttendeeViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: AttendeeViewHolder, position: Int) {
        val countryName: EventAttendee = attendees[position]
        holder.bind(countryName)
    }

    override fun getItemCount(): Int = attendees.size


    inner class AttendeeViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.attendee_list_item, parent, false)) {

        private var title: TextView? = itemView.findViewById(R.id.attendee_name)
        private var mail: TextView? = itemView.findViewById(R.id.attendee_mail)

        fun bind(attendee: EventAttendee) {
            title?.text = if (attendee.name.isNullOrBlank()) {
                attendee.email
            } else {
                attendee.name
            }
            mail?.text =   when (attendee.status) {
                1 -> "ACCEPTED"
                2 -> "DECLINED"
                3 -> "INVITED"
                else -> ""
            }

        }
    }
}