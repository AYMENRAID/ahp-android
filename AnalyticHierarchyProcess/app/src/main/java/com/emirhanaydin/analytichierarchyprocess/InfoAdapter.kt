package com.emirhanaydin.analytichierarchyprocess

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class InfoAdapter(private val context: Context, private val dataSet: List<InfoItem>) :
    RecyclerView.Adapter<InfoAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val inflater = LayoutInflater.from(p0.context)
        val listItem = inflater.inflate(R.layout.info_list_item, p0, false)

        return ViewHolder(listItem)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val infoItem = dataSet[p1]

        val color = ContextCompat.getColor(context, if (p1 % 2 == 0) R.color.colorDefaultText else R.color.colorGray)

        p0.textViewImportance.apply {
            text = infoItem.importance
            setTextColor(color)
        }
        p0.textViewRating.apply {
            text = infoItem.rating
            setTextColor(color)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewImportance: TextView = view.findViewById(R.id.textViewImportance)
        val textViewRating: TextView = view.findViewById(R.id.textViewRating)
    }
}