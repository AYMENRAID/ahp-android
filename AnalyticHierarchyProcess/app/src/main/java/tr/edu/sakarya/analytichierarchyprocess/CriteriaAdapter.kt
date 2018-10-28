package tr.edu.sakarya.analytichierarchyprocess

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CriteriaAdapter(context: Context, resource: Int, private val criteriaList: List<Criteria>) :
    ArrayAdapter<Criteria>(context, resource, criteriaList) {
    var listView: View? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (listView == null) {
            val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            listView = inflater.inflate(R.layout.layout_criteria, parent)
        }

        val textViewCriteria: TextView = listView!!.findViewById(R.id.textViewCriteria)
        val textViewValue: TextView = listView!!.findViewById(R.id.textViewValue)

        val criteria: Criteria = criteriaList[position]

        textViewCriteria.text = criteria.criteria
        textViewValue.text = criteria.value.toString()

        return listView!!
    }
}