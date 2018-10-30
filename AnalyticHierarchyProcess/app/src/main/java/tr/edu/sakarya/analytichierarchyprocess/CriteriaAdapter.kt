package tr.edu.sakarya.analytichierarchyprocess

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CriteriaAdapter(context: Context, resource: Int, private val criteriaList: List<Criteria>) :
    ArrayAdapter<Criteria>(context, resource, criteriaList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        var listView: View? = convertView

        if (listView == null) {
            val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            listView = inflater.inflate(R.layout.layout_criteria, parent, false)

            viewHolder = ViewHolder(
                listView.findViewById(R.id.textViewCriteria),
                listView.findViewById(R.id.textViewValue)
            )

            listView.tag = viewHolder
        } else {
            viewHolder = convertView!!.tag as ViewHolder
        }

        val criteria: Criteria = criteriaList[position]

        viewHolder.textViewCriteria.text = criteria.criteria
        viewHolder.textViewValue.text = criteria.value.toString()

        return listView!!
    }

    private data class ViewHolder(
        val textViewCriteria: TextView,
        val textViewValue: TextView
    )
}