package com.today.calendarevents.details

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.today.calendarevents.R
import com.today.calendarevents.data.EventAttendee
import com.today.calendarevents.databinding.ItemAttendeeBinding

class EventAttendeesAdapter(private val attendees: List<EventAttendee>?) :
    RecyclerView.Adapter<EventAttendeesAdapter.AttendeeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendeeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAttendeeBinding.inflate(inflater, parent, false)
        return AttendeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttendeeViewHolder, position: Int) {
        holder.bind(attendees?.get(position))
    }

    override fun getItemCount(): Int = attendees?.size ?: 0

    inner class AttendeeViewHolder(private val binding: ItemAttendeeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(attendee: EventAttendee?) {
            binding.attendeeName.text = if (attendee?.name?.isEmpty() == true) {
                attendee.email
            } else {
                attendee?.name
            }
            binding.attendeeStatus.text = attendee?.status?.let { itemView.context.resources.getString(it) }
        }
    }
}
