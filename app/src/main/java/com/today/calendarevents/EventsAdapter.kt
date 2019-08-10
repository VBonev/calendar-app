package com.today.calendarevents

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration
import java.util.*

class EventsAdapter(private val events: List<CalendarEvent>, private val headerIndexes: ArrayList<Long>) :
    RecyclerView.Adapter<EventsAdapter.EventViewHolder>(), StickyHeaderAdapter<EventsAdapter.HeaderHolder> {

    override fun getHeaderId(position: Int): Long {
        return if (position < headerIndexes.size)
            headerIndexes[position]
        else
            StickyHeaderDecoration.NO_HEADER_ID
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): HeaderHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_header, parent, false)
        return HeaderHolder(view)
    }

    override fun onBindHeaderViewHolder(headerHolder: HeaderHolder, position: Int) {
        val item = events[position]
        headerHolder.date.text = DateUtils.getDate(item.startDate, "dd  MMM")
    }

    private var itemAction: ((CalendarEvent) -> Unit)? = null

    fun setItemAction(action: (CalendarEvent) -> Unit) {
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

    class HeaderHolder(v: View) : RecyclerView.ViewHolder(v) {
        val date: TextView = v.findViewById(R.id.date)
    }

    inner class EventViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.event_list_item, parent, false)) {

        private var title: TextView? = itemView.findViewById(R.id.event_title)
        private var date: TextView? = itemView.findViewById(R.id.event_date)
        private var calColorView: ImageView? = itemView.findViewById(R.id.calendar_color)

        fun bind(event: CalendarEvent) {
            title?.text = event.name
            date?.text = if (event.allDay == true) {
                itemView.context.getString(R.string.all_day_label)
            } else {
                itemView.context.resources.getString(
                    R.string.time_slot_values,
                    DateUtils.getDate(event.startDate, "hh:mm"),
                    DateUtils.getDate(event.endDate, "hh:mm")
                )
            }
            calColorView?.setBackgroundColor(getDisplayColor(event.calDisplayColor?.toInt()?:0))
            itemView.setOnClickListener { itemAction?.invoke(event) }

        }

        fun getDisplayColor(color: Int): Int {
            val fArr = FloatArray(3)
            Color.colorToHSV(color, fArr)
            if (fArr[2] > 0.79f) {
                fArr[1] = Math.min(fArr[1] * 1.3f, 1.0f)
                fArr[2] = fArr[2] * 0.8f
            }
            return Color.HSVToColor(Color.alpha(color), fArr)
        }
    }
}