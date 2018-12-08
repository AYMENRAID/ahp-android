package com.emirhanaydin.analytichierarchyprocess

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class CriteriaAdapter(private val dataSet: List<Criterion>) :
    RecyclerView.Adapter<CriteriaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val inflater = LayoutInflater.from(p0.context)
        val listItem = inflater.inflate(R.layout.criterion_list_item, p0, false)

        return ViewHolder(listItem)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val criterion = dataSet[p1]

        p0.textViewCriterion.text = criterion.name
        p0.editTextRating.text = criterion.rating.toString()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewCriterion: TextView = view.findViewById(R.id.textViewCriterionName)
        val editTextRating: TextView = view.findViewById(R.id.editTextCriterionRating)
    }
}