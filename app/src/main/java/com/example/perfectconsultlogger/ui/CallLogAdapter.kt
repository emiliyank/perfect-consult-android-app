package com.example.perfectconsultlogger.ui

import android.support.v7.widget.RecyclerView
import android.telecom.Call
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.perfectconsultlogger.R
import com.example.perfectconsultlogger.data.CallLog

class CallLogAdapter(var logs: List<CallLog>) : RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallLogViewHolder =
        CallLogViewHolder(parent.inflate(R.layout.item_call_log))

    override fun getItemCount(): Int = logs.size

    override fun onBindViewHolder(holder: CallLogViewHolder, position: Int) = holder.bind(logs[position])

    private fun ViewGroup.inflate(layoutRes: Int): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, false)
    }


    class CallLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(log: CallLog){

        }

    }
}
