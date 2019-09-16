package com.example.perfectconsultlogger.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.perfectconsultlogger.R

class CallLogAdapter(val logs: List<com.example.perfectconsultlogger.data.CallLog>): RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CallLogViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_call_log, parent, false) as TextView
        return CallLogViewHolder(textView)
    }

    override fun onBindViewHolder(p0: CallLogViewHolder, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun setLogs(data: List<com.example.perfectconsultlogger.data.CallLog>){

    }

    class CallLogViewHolder(view: TextView): RecyclerView.ViewHolder(view) {

    }

    override fun getItemCount() = logs!!.size

}
