package com.emirhanaydin.analytichierarchyprocess

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class CriteriaAdapter(
    private val context: Context,
    private val dataSet: List<Criteria>
) :
    BaseExpandableListAdapter() {
    private var optionsViewOnClickListener: OptionsViewOnClickListener? = null

    companion object {
        const val CHILD_TYPE_OPTIONS = 0
        const val CHILD_TYPE_CRITERION = 1
    }

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
        return dataSet[groupPosition].children.size + 1
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return dataSet[groupPosition].children[childPosition - 1]
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
        val childType = getChildType(groupPosition, childPosition)
        val view = if (convertView == null || (convertView.tag as ChildViewHolder).childType != childType) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view: View

            val viewHolder: ChildViewHolder

            when (childType) {
                CHILD_TYPE_OPTIONS -> {
                    view = inflater.inflate(R.layout.criterion_list_options, parent, false)

                    viewHolder = OptionsViewHolder(
                        view.findViewById(R.id.buttonCriterionAlternatives),
                        view.findViewById(R.id.buttonCriterionDelete)
                    )

                    viewHolder.buttonCriterionDelete.isEnabled = false
                    viewHolder.buttonCriterionAlternatives.apply {
                        tag = Position(groupPosition, childPosition)
                        setOnClickListener(optionsViewOnClickListener)
                    }
                }
                CHILD_TYPE_CRITERION -> {
                    view = inflater.inflate(R.layout.criterion_list_item, parent, false)

                    viewHolder = CriterionViewHolder(
                        view.findViewById(R.id.textViewCriterionItemName),
                        view.findViewById(R.id.editTextCriterionRating)
                    )

                    viewHolder.editTextCriterionRating.filters = arrayOf(
                        InputFilterMinMax(
                            CriteriaActivity.CRITERION_RATING_MIN,
                            CriteriaActivity.CRITERION_RATING_MAX
                        )
                    )
                }
                else -> {
                    throw IllegalStateException("Illegal child type")
                }
            }

            view.tag = viewHolder

            view
        } else convertView

        val viewHolder = view.tag as ChildViewHolder

        when (childType) {
            CHILD_TYPE_OPTIONS -> {
                viewHolder as OptionsViewHolder

                val position = viewHolder.buttonCriterionAlternatives.tag as Position
                position.apply {
                    this.groupPosition = groupPosition
                    this.childPosition = childPosition
                }
            }
            CHILD_TYPE_CRITERION -> {
                val criterion = getChild(groupPosition, childPosition) as Criterion

                viewHolder as CriterionViewHolder
                viewHolder.textViewCriterionItemName.text = criterion.name
                viewHolder.editTextCriterionRating.setText(criterion.rating.toString())
            }
        }

        return view
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return dataSet.size
    }

    override fun getChildType(groupPosition: Int, childPosition: Int): Int {
        return if (childPosition <= 0) CHILD_TYPE_OPTIONS else CHILD_TYPE_CRITERION
    }

    override fun getChildTypeCount(): Int {
        return 2
    }

    fun setOptionsViewOnClickListener(listener: OptionsViewOnClickListener) {
        optionsViewOnClickListener = listener
    }

    interface OptionsViewOnClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            val position = v.tag as Position

            onClick(position.groupPosition, position.childPosition)
        }

        fun onClick(groupPosition: Int, childPosition: Int)
    }

    private abstract class ChildViewHolder(
        val childType: Int
    )

    private data class GroupViewHolder(
        val textViewCriterionGroupName: TextView
    )

    private data class CriterionViewHolder(
        val textViewCriterionItemName: TextView,
        val editTextCriterionRating: EditText
    ) : ChildViewHolder(CHILD_TYPE_OPTIONS)

    private data class OptionsViewHolder(
        val buttonCriterionAlternatives: Button,
        val buttonCriterionDelete: Button
    ) : ChildViewHolder(CHILD_TYPE_CRITERION)

    private data class Position(
        var groupPosition: Int,
        var childPosition: Int
    )
}