package tr.edu.sakarya.analytichierarchyprocess

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CriterionAdapter(context: Context, resource: Int, private val criterionList: List<Criterion>) :
    ArrayAdapter<Criterion>(context, resource, criterionList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        var listView: View? = convertView

        if (listView == null) {
            val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            listView = inflater.inflate(R.layout.layout_criterion, parent, false)

            viewHolder = ViewHolder(
                listView.findViewById(R.id.textViewCriteria),
                listView.findViewById(R.id.textViewValue)
            )

            listView.tag = viewHolder
        } else
            viewHolder = listView.tag as ViewHolder

        val criterion: Criterion = criterionList[position]

        viewHolder.textViewCriteria.text = criterion.criterion
        viewHolder.textViewValue.text = criterion.value.toString()

        return listView!!
    }

    private data class ViewHolder(
        val textViewCriteria: TextView,
        val textViewValue: TextView
    )
}