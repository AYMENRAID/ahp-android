package com.emirhanaydin.analytichierarchyprocess

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlin.math.absoluteValue

class CriteriaAdapter(
    private val context: Context,
    private val criteriaList: List<Criteria>
) :
    BaseExpandableListAdapter() {
    override fun getGroup(groupPosition: Int): Any {
        return criteriaList[groupPosition]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        var cView: View? = convertView
        val groupViewHolder: GroupViewHolder

        if (cView == null) {
            val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            cView = inflater.inflate(R.layout.criterion_expandable_list_group, parent, false)

            groupViewHolder = GroupViewHolder(cView.findViewById(R.id.text_view_criteria))

            cView.tag = groupViewHolder
        } else
            groupViewHolder = cView.tag as GroupViewHolder

        groupViewHolder.textViewCriteria.text = (getGroup(groupPosition) as Criteria).parent

        return cView!!
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return criteriaList[groupPosition].children.size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return criteriaList[groupPosition].children[childPosition]
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
        var cView: View? = convertView
        val viewHolder: ChildViewHolder

        if (cView == null) {
            val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            cView = inflater.inflate(R.layout.criterion_expandable_list_item, parent, false)

            val textViewCriteria: TextView = cView.findViewById(R.id.text_view_criteria)
            val editTextValue: EditText = cView.findViewById(R.id.edit_text_value)
            val switchValueSign: Switch = cView.findViewById(R.id.switch_value_sign)
            viewHolder = ChildViewHolder(
                textViewCriteria,
                editTextValue,
                switchValueSign
            )
            editTextValue.filters = arrayOf(
                InputFilterMinMax(
                    CriteriaActivity.CRITERION_VALUE_MIN,
                    CriteriaActivity.CRITERION_VALUE_MAX
                )
            )
            editTextValue.tag = EditTextValueTag(
                EditTextValueTextWatcher(editTextValue),
                Position(groupPosition, childPosition)
            )

            cView.tag = viewHolder
        } else
            viewHolder = cView.tag as ChildViewHolder

        val criterion: Criterion = getChild(groupPosition, childPosition) as Criterion
        val position = Position(groupPosition, childPosition)

        viewHolder.textViewCriteria.text = criterion.criterion

        val editTextValue: EditText = viewHolder.editTextValue
        val editTextValueTag = editTextValue.tag as EditTextValueTag
        val textWatcher = editTextValueTag.textWatcher
        editTextValue.removeTextChangedListener(textWatcher)
        editTextValue.setText(criterion.value.absoluteValue.toString())
        editTextValue.addTextChangedListener(textWatcher)
        editTextValueTag.position = position

        val switchValueSign = viewHolder.switchValueSign
        val negative = criterion.value < 0
        switchValueSign.setOnCheckedChangeListener(null)
        switchValueSign.isChecked = negative
        switchValueSign.jumpDrawablesToCurrentState()
        switchValueSign.text = context.getString(if (negative) R.string.negative else R.string.positive)
        switchValueSign.tag = position
        switchValueSign.setOnCheckedChangeListener(switchValueSignOnChangeListener)

        return cView!!
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return criteriaList.size
    }

    private val switchValueSignOnChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        val position = buttonView.tag as Position
        val criterion = getChild(position.groupPosition, position.childPosition) as Criterion

        val abs = criterion.value.absoluteValue
        if (isChecked) {
            criterion.value = -abs
            buttonView.text = context.getString(R.string.negative)
        } else {
            criterion.value = abs
            buttonView.text = context.getString(R.string.positive)
        }
    }

    private inner class EditTextValueTextWatcher(private val view: View) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val tag = view.tag as EditTextValueTag? ?: return
            val position = tag.position
            val criterion = getChild(position.groupPosition, position.childPosition) as Criterion

            criterion.value = s.toString().toIntOrNull() ?: return
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private data class ChildViewHolder(
        val textViewCriteria: TextView,
        val editTextValue: EditText,
        val switchValueSign: Switch
    )

    private data class GroupViewHolder(
        val textViewCriteria: TextView
    )

    private data class Position(
        val groupPosition: Int,
        val childPosition: Int
    )

    private data class EditTextValueTag(
        val textWatcher: EditTextValueTextWatcher,
        var position: Position
    )
}