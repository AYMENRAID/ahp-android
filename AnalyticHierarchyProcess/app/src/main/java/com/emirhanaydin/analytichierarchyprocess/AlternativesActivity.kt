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
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_alternatives.*

class AlternativesActivity : AppCompatActivity() {
    private lateinit var alternativesList: MutableList<Alternatives>
    private lateinit var alternativesAdapter: AlternativesAdapter

    companion object {
        const val ALTERNATIVE_NAME_MAX_LENGTH = 20
        const val ALTERNATIVE_VALUE_MIN = 1
        const val ALTERNATIVE_VALUE_MAX = 9
        const val EXTRA_CRITERION = "Criterion"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alternatives)

        alternativesList = mutableListOf()
        alternativesAdapter = AlternativesAdapter(this, alternativesList)
        expandableListViewAlternatives.setAdapter(alternativesAdapter)

        buttonAddAlternative.setOnClickListener(onClickAddAlternative)

        buttonCalculate.setOnClickListener(onClickButtonCalculate)
    }

    private val onClickAddAlternative = View.OnClickListener {
        val inputAlternative = EditText(this)
        inputAlternative.inputType = InputType.TYPE_CLASS_TEXT
        inputAlternative.filters = arrayOf(InputFilter.LengthFilter(ALTERNATIVE_NAME_MAX_LENGTH))
        inputAlternative.hint = getString(R.string.alternative)

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.add_alternative))
        builder.setView(inputAlternative)

        builder.setPositiveButton(getString(R.string.add)) { _, _ ->
            val input = inputAlternative.text.toString().trim()
            if (input.isEmpty()) {
                Toast.makeText(this, getString(R.string.warning_no_alternative), Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            if (alternativesList.any { alternative -> alternative.parent.equals(input, true) }) {
                Toast.makeText(this, getString(R.string.warning_alternative_exists), Toast.LENGTH_LONG).show()
                return@setPositiveButton
            }

            val alternative = Alternative(input)
            for (alternatives in alternativesList) {
                alternatives.children.add(Alternative(alternative.name))
            }

            alternativesList.add(Alternatives(input, mutableListOf()))
            alternativesAdapter.notifyDataSetChanged()
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }

        val alertDialog: AlertDialog = builder.create()

        alertDialog.setOnShowListener {
            inputAlternative.postDelayed({
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
            }, 100)
        }

        alertDialog.show()
    }

    private val onClickButtonCalculate = View.OnClickListener {
        val size = alternativesList.size
        if (size < 1) {
            Toast.makeText(this, getString(R.string.warning_no_alternative), Toast.LENGTH_SHORT).show()
            return@OnClickListener
        }
        if (size < 2) {
            Toast.makeText(this, getString(R.string.warning_need_two_alternatives), Toast.LENGTH_LONG).show()
            return@OnClickListener
        }

        val weights = Array(size) { FloatArray(size) }
        val subtotals = FloatArray(size)

        // Calculate the weights
        for (i in 0 until size) {
            val children = alternativesList[i].children

            for (j in 0 until children.size) {
                val value = children[j].value.toFloat()
                val index = j + i + 1 // Plus i to make the calculations triangular, plus 1 to skip diagonal

                weights[i][index] = if (value > 0) value else 1 / -value // Take absolute reciprocal if negative
                weights[index][i] = 1 / weights[i][index] // Reciprocal

                // Add the values to subtotals by their column indexes
                subtotals[index] += weights[i][index]
                subtotals[i] += weights[index][i]
            }
            // The diagonal indicates the alternative itself, so its weight is 1
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
        intent.putExtra(
            ResultActivity.ALTERNATIVES,
            alternativesList.map { alternatives -> alternatives.parent }.toTypedArray()
        )
        intent.putExtra(ResultActivity.PRIORITIES, priorities)
        intent.putExtra(ResultActivity.CONSISTENCY_RATIO, cr)
        startActivity(intent)
    }
}
