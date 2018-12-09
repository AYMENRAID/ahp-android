package com.emirhanaydin.analytichierarchyprocess

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.EditText
import android.widget.TextView

class CriteriaAdapter(
    private val context: Context,
    private val dataSet: List<Criteria>
) :
    BaseExpandableListAdapter() {

    override fun getGroup(groupPosition: Int): Any {
        return dataSet[groupPosition]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: let {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.criterion_list_group, parent, false)

            view.tag = GroupViewHolder(view.findViewById(R.id.textViewCriterionGroupName))

            view
        }

        val groupViewHolder: GroupViewHolder = view.tag as GroupViewHolder
        groupViewHolder.textViewCriterionGroupName.text = (getGroup(groupPosition) as Criteria).parentName

        return view
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return dataSet[groupPosition].children.size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return dataSet[groupPosition].children[childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = convertView ?: let {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.criterion_list_item, parent, false)

            val viewHolder = ChildViewHolder(
                view.findViewById(R.id.textViewCriterionItemName),
                view.findViewById(R.id.editTextCriterionRating)
            )

            viewHolder.editTextCriterionRating.filters = arrayOf(
                InputFilterMinMax(
                    CriteriaActivity.CRITERION_RATING_MIN,
                    CriteriaActivity.CRITERION_RATING_MAX
                )
            )

            view.tag = viewHolder

            view
        }

        val viewHolder = view.tag as ChildViewHolder
        val criterion = getChild(groupPosition, childPosition) as Criterion

        viewHolder.textViewCriterionItemName.text = criterion.name
        viewHolder.editTextCriterionRating.setText(criterion.rating.toString())

        return view
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return dataSet.size
    }

    private data class GroupViewHolder(
        val textViewCriterionGroupName: TextView
    )

    private data class ChildViewHolder(
        val textViewCriterionItemName: TextView,
        val editTextCriterionRating: EditText
    )
}