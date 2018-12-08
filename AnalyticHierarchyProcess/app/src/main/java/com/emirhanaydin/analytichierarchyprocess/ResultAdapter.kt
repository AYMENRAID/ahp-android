package com.emirhanaydin.analytichierarchyprocess

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.text.NumberFormat

class ResultAdapter(private val dataSet: List<AlternativePriority>) :
    RecyclerView.Adapter<ResultAdapter.ViewHolder>() {
    private val priorityFormat = NumberFormat.getPercentInstance().apply {
        minimumFractionDigits = 2
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val inflater = LayoutInflater.from(p0.context)
        val listItem = inflater.inflate(R.layout.result_list_item, p0, false)

        return ViewHolder(listItem)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val alternativePriority = dataSet[p1]

        p0.textViewAlternative.text = alternativePriority.alternative
        p0.textViewPriority.text = priorityFormat.format(alternativePriority.priority)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewAlternative: TextView = view.findViewById(R.id.textViewResultAlternative)
        val textViewPriority: TextView = view.findViewById(R.id.text_view_result_priority)
    }
}