package tr.edu.sakarya.analytichierarchyprocess

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.text.InputType
import android.widget.EditText
import android.widget.ExpandableListView
import kotlinx.android.synthetic.main.activity_criteria.*

class CriteriaActivity : AppCompatActivity() {
    private lateinit var criteria: MutableList<Criterion>
    private lateinit var criteriaChildren: MutableMap<Criterion, MutableList<Criterion>>
    private lateinit var criteriaAdapter: CriteriaAdapter
    private lateinit var listViewCriteria: ExpandableListView
    private var expandedCriteriaGroup = -1

    companion object {
        const val CRITERIA_NAME_MAX_LENGTH = 20
        const val CRITERIA_VALUE_MIN = 1
        const val CRITERIA_VALUE_MAX = 9
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criteria)

        criteria = mutableListOf()
        criteriaChildren = mutableMapOf()
        criteriaAdapter = CriteriaAdapter(this, criteria, criteriaChildren)
        listViewCriteria = findViewById(R.id.expandable_list_view_criteria)
        listViewCriteria.setAdapter(criteriaAdapter)

        buttonAddCriteria.setOnClickListener {
            onClickAddNewCriteria()
        }

        listViewCriteria.setOnGroupExpandListener { groupPosition ->
            val previousPosition = expandedCriteriaGroup
            expandedCriteriaGroup = groupPosition

            if (previousPosition < 0) return@setOnGroupExpandListener

            listViewCriteria.collapseGroup(previousPosition)
        }

        listViewCriteria.setOnGroupCollapseListener { groupPosition ->
            if (expandedCriteriaGroup == groupPosition)
                expandedCriteriaGroup = -1
        }
    }

    private fun onClickAddNewCriteria() {
        val inputCriteria = EditText(this)
        inputCriteria.inputType = InputType.TYPE_CLASS_TEXT
        inputCriteria.filters = arrayOf(InputFilter.LengthFilter(CRITERIA_NAME_MAX_LENGTH))
        inputCriteria.hint = getString(R.string.criterion)

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.add_criterion))
        builder.setView(inputCriteria)

        builder.setPositiveButton(getString(R.string.add)) { _, _ ->
            val input = inputCriteria.text.toString()
            val criterion = Criterion(input)

            criteriaChildren[criterion] = mutableListOf()
            for (group in criteria) {
                criteriaChildren[group]?.add(criterion)
            }
            criteria.add(criterion)

            criteriaAdapter.notifyDataSetChanged()
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}
