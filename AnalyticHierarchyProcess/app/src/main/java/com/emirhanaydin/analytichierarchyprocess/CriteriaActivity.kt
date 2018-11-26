package com.emirhanaydin.analytichierarchyprocess

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ExpandableListView
import android.widget.Toast
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
            val input = inputCriteria.text.toString().trim()
            if (input.isEmpty()) {
                Toast.makeText(this, getString(R.string.warning_empty_criterion), Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            if (criteriaList.any { criteria -> criteria.parent.equals(input, true) }) {
                Toast.makeText(this, getString(R.string.warning_criterion_exists), Toast.LENGTH_LONG).show()
                return@setPositiveButton
            }

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
            inputCriteria.postDelayed({
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
            }, 100)
        }

        alertDialog.show()
    }

    private val onClickButtonCalculate = View.OnClickListener {
        val size = criteriaList.size
        val weights = Array(size) { FloatArray(size) }
        val subtotals = FloatArray(size)

        // Calculate the weights
        for (i in 0 until size) {
            val children = criteriaList[i].children

            for (j in 0 until children.size) {
                val value = children[j].value.toFloat()
                val index = j + i + 1 // Plus i to make the calculations triangular, plus 1 to skip diagonal

                weights[i][index] = if (value > 0) value else 1 / -value // Take absolute reciprocal if negative
                weights[index][i] = 1 / weights[i][index] // Reciprocal

                // Add the values to subtotals by their column indexes
                subtotals[index] += weights[i][index]
                subtotals[i] += weights[index][i]
            }
            // The diagonal indicates the criterion itself, so its weight is 1
            weights[i][i] = 1f
            subtotals[i] += weights[i][i]
        }

        // Normalize the weights
        for (i in 0 until size) {
            for (j in 0 until size) {
                weights[i][j] /= subtotals[j]
            }
        }

        // Calculate priorities with the normalized weights
        val priorities = FloatArray(size)
        for (i in 0 until size) {
            var sum = 0f
            for (j in 0 until size) {
                sum += weights[i][j]
            }
            priorities[i] = sum / size // Average of the row
        }

        // Calculate the consistency ratio
        val eMax = multiplyVectors(priorities, subtotals)
        val ci = (eMax - size) / (size - 1)
        val cr = ci / getRandomIndex(size)

        // Start ResultActivity with the calculated results
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.CRITERIA, criteriaList.map { criteria -> criteria.parent }.toTypedArray())
        intent.putExtra(ResultActivity.PRIORITIES, priorities)
        intent.putExtra(ResultActivity.CONSISTENCY_RATIO, cr)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}
