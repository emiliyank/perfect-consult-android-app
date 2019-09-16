package com.example.perfectconsultlogger.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.perfectconsultlogger.R
import kotlinx.android.synthetic.main.item_call_log.view.*

class CallLogAdapter(val logs: List<com.example.perfectconsultlogger.data.CallLog>): RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CallLogViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_call_log, parent, false) as TextView
        return CallLogViewHolder(textView)
    }

    override fun onBindViewHolder(holder: CallLogViewHolder, position: Int) {
        holder.itemView.txt_number.text = logs[position].number
    }

    override fun getItemCount() = logs!!.size

    class CallLogViewHolder(view: TextView): RecyclerView.ViewHolder(view) {

    }
}
