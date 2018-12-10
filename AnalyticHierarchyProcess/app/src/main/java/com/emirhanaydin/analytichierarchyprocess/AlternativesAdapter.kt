package com.emirhanaydin.analytichierarchyprocess

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class AlternativesAdapter(
    private val context: Context,
    private val alternativesList: List<Alternatives>
) :
    BaseExpandableListAdapter() {
    override fun getGroup(groupPosition: Int): Any {
        return alternativesList[groupPosition]
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
            cView = inflater.inflate(R.layout.ahp_list_group, parent, false)

            groupViewHolder = GroupViewHolder(cView.findViewById(R.id.textViewGroupName))

            cView.tag = groupViewHolder
        } else
            groupViewHolder = cView.tag as GroupViewHolder

        groupViewHolder.textViewGroupName.text = (getGroup(groupPosition) as Alternatives).parentName

        return cView!!
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return alternativesList[groupPosition].children.size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return alternativesList[groupPosition].children[childPosition]
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
            cView = inflater.inflate(R.layout.ahp_list_item, parent, false)

            val textViewAlternative: TextView = cView.findViewById(R.id.textViewItemName)
            val editTextRating: EditText = cView.findViewById(R.id.editTextRating)
            val switchReciprocal: Switch = cView.findViewById(R.id.switchReciprocal)
            viewHolder = ChildViewHolder(
                textViewAlternative,
                editTextRating,
                switchReciprocal
            )
            editTextRating.filters = arrayOf(
                InputFilterMinMax(
                    AlternativesActivity.ALTERNATIVE_VALUE_MIN,
                    AlternativesActivity.ALTERNATIVE_VALUE_MAX
                )
            )
            editTextRating.tag = EditTextRatingTag(
                EditTextRatingTextWatcher(editTextRating),
                Position(groupPosition, childPosition)
            )

            cView.tag = viewHolder
        } else
            viewHolder = cView.tag as ChildViewHolder

        val alternative: Alternative = getChild(groupPosition, childPosition) as Alternative
        val position = Position(groupPosition, childPosition)

        viewHolder.textViewAlternative.text = alternative.name

        val editTextRating: EditText = viewHolder.editTextRating
        val editTextRatingTag = editTextRating.tag as EditTextRatingTag
        val textWatcher = editTextRatingTag.textWatcher
        editTextRating.removeTextChangedListener(textWatcher)
        editTextRating.setText(alternative.rating.toString())
        editTextRating.addTextChangedListener(textWatcher)
        editTextRatingTag.position = position

        val switchReciprocal = viewHolder.switchReciprocal
        val isReciprocal = alternative.isReciprocal
        switchReciprocal.setOnCheckedChangeListener(null)
        switchReciprocal.isChecked = isReciprocal
        switchReciprocal.jumpDrawablesToCurrentState()
        switchReciprocal.text = context.getString(if (isReciprocal) R.string.reciprocal else R.string.normal)
        switchReciprocal.tag = position
        switchReciprocal.setOnCheckedChangeListener(switchReciprocalOnChangeListener)

        return cView!!
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return alternativesList.size
    }

    private val switchReciprocalOnChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        val position = buttonView.tag as Position
        val alternative = getChild(position.groupPosition, position.childPosition) as Alternative
        alternative.isReciprocal = isChecked

        if (isChecked) {
            buttonView.text = context.getString(R.string.reciprocal)
        } else {
            buttonView.text = context.getString(R.string.normal)
        }
    }

    private inner class EditTextRatingTextWatcher(private val view: View) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val tag = view.tag as EditTextRatingTag? ?: return
            val position = tag.position
            val alternative = getChild(position.groupPosition, position.childPosition) as Alternative

            alternative.rating = s.toString().toIntOrNull() ?: return
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private data class ChildViewHolder(
        val textViewAlternative: TextView,
        val editTextRating: EditText,
        val switchReciprocal: Switch
    )

    private data class GroupViewHolder(
        val textViewGroupName: TextView
    )

    private data class Position(
        val groupPosition: Int,
        val childPosition: Int
    )

    private data class EditTextRatingTag(
        val textWatcher: EditTextRatingTextWatcher,
        var position: Position
    )
}