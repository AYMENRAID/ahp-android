package tr.edu.sakarya.analytichierarchyprocess

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView

class CriteriaAdapter(
    private val context: Context,
    private val criteria: List<Criterion>,
    private val criteriaChildren: Map<Criterion, List<Criterion>>
) :
    BaseExpandableListAdapter() {

    companion object {
        @JvmStatic
        private val TAG: String = CriteriaAdapter::class.java.name
    }

    override fun getGroup(groupPosition: Int): Any {
        return criteria[groupPosition]
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

            groupViewHolder =
                    GroupViewHolder(cView.findViewById(R.id.textViewCriteria))

            cView.tag = groupViewHolder
        } else
            groupViewHolder = cView.tag as GroupViewHolder

        val criterion: Criterion = getGroup(groupPosition) as Criterion
        groupViewHolder.textViewCriteria.text = criterion.criterion

        return cView!!
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return criteriaChildren[criteria[groupPosition]]?.size ?: 0
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        val children = criteriaChildren[criteria[groupPosition]]

        return children?.get(childPosition)
            ?: throw NullPointerException("$TAG: ${criteria[groupPosition]}: at $childPosition")
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

            val switchValueSign: Switch = cView.findViewById(R.id.switch_value_sign)
            viewHolder = ChildViewHolder(
                cView.findViewById(R.id.textViewCriteria),
                cView.findViewById(R.id.editTextValue),
                switchValueSign
            )

            switchValueSign.setOnCheckedChangeListener { _, isChecked ->
                switchValueSign.text = context.getString(if (isChecked) R.string.negative else R.string.positive)
            }

            cView.tag = viewHolder
        } else
            viewHolder = cView.tag as ChildViewHolder

        val criterion: Criterion = getChild(groupPosition, childPosition) as Criterion
        viewHolder.textViewCriteria.text = criterion.criterion
        val editTextValue = viewHolder.editTextValue
        editTextValue.setText(criterion.value.toString(), TextView.BufferType.EDITABLE)
        editTextValue.filters =
                arrayOf(InputFilterMinMax(CriteriaActivity.CRITERIA_VALUE_MIN, CriteriaActivity.CRITERIA_VALUE_MAX))

        return cView!!
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return criteriaChildren.size
    }

    private data class ChildViewHolder(
        val textViewCriteria: TextView,
        val editTextValue: EditText,
        val switchValueSign: Switch
    )

    private data class GroupViewHolder(
        val textViewCriteria: TextView
    )
}