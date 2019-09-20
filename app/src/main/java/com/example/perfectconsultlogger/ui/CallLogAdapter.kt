package com.example.perfectconsultlogger.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.perfectconsultlogger.R
import com.example.perfectconsultlogger.data.CallLog
import kotlinx.android.synthetic.main.item_call_log.view.*

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
            with(itemView){
                txt_number.text = log.target_number
                txt_start_time.text = log.startTime
                txt_duration.text = log.duration
                if(log.isIncoming){
                    img_call.setImageResource(R.drawable.ic_incoming)
                }else{
                    img_call.setImageResource(R.drawable.ic_outgoing)
                }
            }
        }

    }
}
