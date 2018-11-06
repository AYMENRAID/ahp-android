package tr.edu.sakarya.analytichierarchyprocess

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.text.InputType
import android.widget.EditText
import android.widget.ExpandableListView
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_criteria.*

class CriteriaActivity : AppCompatActivity() {
    private lateinit var criteria: MutableList<Criterion>
    private lateinit var criteriaChildren: MutableMap<Criterion, MutableList<Criterion>>

    companion object {
        const val CRITERIA_NAME_MAX_LENGTH = 20
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criteria)

        criteria = mutableListOf()
        criteriaChildren = mutableMapOf()
        val criteriaAdapter = CriteriaAdapter(this, criteria, criteriaChildren)
        val listViewCriteria: ExpandableListView = findViewById(R.id.expandable_list_view_criteria)
        listViewCriteria.setAdapter(criteriaAdapter)

        buttonAddCriteria.setOnClickListener {
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.VERTICAL

            val inputCriteria = EditText(this)
            inputCriteria.inputType = InputType.TYPE_CLASS_TEXT
            inputCriteria.filters = arrayOf(InputFilter.LengthFilter(CRITERIA_NAME_MAX_LENGTH))
            inputCriteria.hint = getString(R.string.criterion)

            linearLayout.addView(inputCriteria)

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.add_criterion))
            builder.setView(linearLayout)

            builder.setPositiveButton(getString(R.string.add)) { _, _ ->
                val criteriaInput = inputCriteria.text.toString()
                val criterion = Criterion(criteriaInput)
                criteria.add(criterion)
                criteriaChildren[criterion] = mutableListOf()

                criteriaAdapter.notifyDataSetChanged()
            }
            builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }

            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }
    }
}
