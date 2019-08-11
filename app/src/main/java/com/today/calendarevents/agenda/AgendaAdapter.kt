package com.today.calendarevents.agenda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration
import com.today.calendarevents.data.CalendarEvent
import com.today.calendarevents.R
import com.today.calendarevents.Utils
import com.today.calendarevents.Utils.HOURS_PATTERN
import com.today.calendarevents.databinding.ItemEventBinding
import java.util.*

class AgendaAdapter(private val events: List<CalendarEvent>, private val headerIndexes: ArrayList<Long>) :
    RecyclerView.Adapter<AgendaAdapter.EventViewHolder>(),
    StickyHeaderAdapter<AgendaAdapter.HeaderHolder> {

    override fun getHeaderId(position: Int): Long {
        return if (position < headerIndexes.size)
            headerIndexes[position]
        else
            StickyHeaderDecoration.NO_HEADER_ID
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): HeaderHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false)
        return HeaderHolder(view)
    }

    override fun onBindHeaderViewHolder(headerHolder: HeaderHolder, position: Int) {
        val item = events[position]
        headerHolder.date.text = Utils.getDate(item.startDate, Utils.DAY_PATTERN)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEventBinding.inflate(inflater, parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val countryName: CalendarEvent = events[position]
        holder.bind(countryName)
    }

    override fun getItemCount(): Int = events.size

    class HeaderHolder(v: View) : RecyclerView.ViewHolder(v) {
        val date: TextView = v.findViewById(R.id.date)
    }

    inner class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: CalendarEvent) {
            binding.calendarColor.setBackgroundColor(Utils.getDisplayColor(event.calDisplayColor))
            binding.eventTitle.text = event.name
            binding.eventDate.text = if (event.allDay == true) {
                itemView.context.getString(R.string.all_day_label)
            } else {
                itemView.context.resources.getString(
                    R.string.time_slot_values,
                    Utils.getDate(event.startDate, HOURS_PATTERN),
                    Utils.getDate(event.endDate, HOURS_PATTERN)
                )
            }

            binding.root.setOnClickListener {
                event.let { movie ->
                    val navigationDirection = AgendaFragmentDirections.actionHomeToDetails(movie)
                    Navigation.findNavController(it).navigate(navigationDirection)
                }
            }
        }
    }
}