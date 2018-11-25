package tr.edu.sakarya.analytichierarchyprocess

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ExpandableListView
import kotlinx.android.synthetic.main.activity_criteria.*

class CriteriaActivity : AppCompatActivity() {
    private lateinit var criteriaList: MutableList<Criteria>
    private lateinit var criteriaAdapter: CriteriaAdapter
    private lateinit var listViewCriteria: ExpandableListView

    companion object {
        const val CRITERIA_NAME_MAX_LENGTH = 20
        const val CRITERIA_VALUE_MIN = 1
        const val CRITERIA_VALUE_MAX = 9
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criteria)

        criteriaList = mutableListOf()
        criteriaAdapter = CriteriaAdapter(this, criteriaList)
        listViewCriteria = findViewById(R.id.expandable_list_view_criteria)
        listViewCriteria.setAdapter(criteriaAdapter)

        buttonAddCriteria.setOnClickListener(onClickAddNewCriteria)

        buttonCalculate.setOnClickListener(onClickButtonCalculate)
    }

    private val onClickAddNewCriteria = View.OnClickListener {
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
            for (criteria in criteriaList) {
                criteria.children.add(Criterion(criterion.criterion))
            }

            criteriaList.add(Criteria(input, mutableListOf()))
            criteriaAdapter.notifyDataSetChanged()
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }

        val alertDialog: AlertDialog = builder.create()

        alertDialog.setOnShowListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
        }

        alertDialog.show()
    }

    private val onClickButtonCalculate = View.OnClickListener {
        val size = criteriaList.size
        val factorWeights = Array(size) { FloatArray(size) }
        val subtotals = FloatArray(size)

        for (i in 0 until size) {
            val children = criteriaList[i].children

            for (j in 0 until children.size) {
                val value = children[j].value.toFloat()
                val index = j + i + 1

                factorWeights[i][index] = if (value > 0) value else 1 / -value
                factorWeights[index][i] = 1 / factorWeights[i][index]

                subtotals[index] += factorWeights[i][index]
                subtotals[i] += factorWeights[index][i]
            }
            factorWeights[i][i] = 1f
            subtotals[i] += factorWeights[i][i]
        }


        val normalizedWeights = Array(size) { FloatArray(size) }

        for (i in 0 until size) {
            for (j in 0 until size) {
                normalizedWeights[i][j] = factorWeights[i][j] / subtotals[j]
            }
        }


        val priorities = FloatArray(size)

        for (i in 0 until size) {
            var sum = 0f
            for (j in 0 until size) {
                sum += normalizedWeights[i][j]
            }
            priorities[i] = sum / size
        }
    }
}
