package com.today

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.today.calendarevents.CalendarEvent
import com.today.calendarevents.R
import java.text.SimpleDateFormat
import java.util.*

class EventsAdapter(private val events: List<CalendarEvent>)
    : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    private var itemAction: ((String) -> Unit)? = null

    fun setItemAction(action: (String) -> Unit) {
        this.itemAction = action
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return EventViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val countryName: CalendarEvent = events[position]
        holder.bind(countryName)
    }

    override fun getItemCount(): Int = events.size


    inner class EventViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.event_list_item, parent, false)) {

        private var title: TextView? = null
        private var date: TextView? = null


        init {
            title = itemView.findViewById(R.id.event_title)
            date = itemView.findViewById(R.id.event_date)

        }

        fun bind(event: CalendarEvent) {
            title?.text = event.name
            date?.text = getDate(event.startDate?.toLong())
            itemView.setOnClickListener { itemAction?.invoke(event.name) }

        }
    }


    private fun getDate(milliSeconds: Long?): String {
        val formatter = SimpleDateFormat("dd/MMM/yy hh:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        if (milliSeconds != null) {
            calendar.timeInMillis = milliSeconds
        }
        return formatter.format(calendar.time)
    }
}