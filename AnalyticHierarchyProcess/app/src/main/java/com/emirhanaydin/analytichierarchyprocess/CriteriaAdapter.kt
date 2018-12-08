package com.emirhanaydin.analytichierarchyprocess

import android.support.v7.widget.RecyclerView
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class CriteriaAdapter(private val dataSet: List<Criterion>) :
    RecyclerView.Adapter<CriteriaAdapter.ViewHolder>() {
    private val selectedItems = SparseBooleanArray()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val inflater = LayoutInflater.from(p0.context)
        val listItem = inflater.inflate(R.layout.criterion_list_item, p0, false)

        return ViewHolder(listItem)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val criterionPriority = dataSet[p1]

        p0.rootLayout.isSelected = selectedItems.get(p1, false)

        p0.textViewCriterion.text = criterionPriority.name
        p0.editTextRating.text = criterionPriority.rating.toString()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val rootLayout: LinearLayout = view.findViewById(R.id.linearLayoutCriterionListItem)
        val textViewCriterion: TextView = view.findViewById(R.id.textViewCriterionName)
        val editTextRating: TextView = view.findViewById(R.id.editTextCriterionRating)

        override fun onClick(v: View?) {
            val position = adapterPosition

            if (selectedItems.get(position, false)) {
                selectedItems.delete(position)
                rootLayout.isSelected = false
            } else {
                selectedItems.put(position, true)
                rootLayout.isSelected = true
            }
        }
    }
}