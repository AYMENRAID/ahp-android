package com.emirhanaydin.analytichierarchyprocess

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_criteria.*

class CriteriaActivity : AppCompatActivity() {
    private lateinit var criteriaList: MutableList<Criteria>
    private lateinit var criteriaAdapter: CriteriaAdapter
    private lateinit var alternativesList: ArrayList<Alternatives>
    private var selectedCriterionPosition = -1

    companion object {
        @JvmStatic
        val TAG: String = CriteriaActivity::class.java.name
        const val CRITERION_NAME_MAX_LENGTH = 20
        const val CRITERION_RATING_MIN = 1
        const val CRITERION_RATING_MAX = 9
        const val EXTRA_ALTERNATIVES = "ExtraAlternatives"
        const val REQUEST_ALTERNATIVES = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criteria)

        criteriaList = mutableListOf()
        criteriaAdapter = CriteriaAdapter(this, criteriaList)
        expandableListViewCriteria.setAdapter(criteriaAdapter)

        criteriaAdapter.setOptionsViewOnClickListener(optionsViewOnClickListener)

        buttonAddCriterion.setOnClickListener(onClickAddCriterion)
        buttonCriteriaCalculate.setOnClickListener(onClickButtonCalculate)
        buttonCriteriaBack.setOnClickListener { finish() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ALTERNATIVES) {
            if (resultCode == Activity.RESULT_OK) {
                val alternativesList = if (data == null) run { Log.e(TAG, "Data intent is null"); return }
                else data.getParcelableArrayListExtra<Alternatives>(EXTRA_ALTERNATIVES)

                val criteria = criteriaList.getOrNull(selectedCriterionPosition) ?: run {
                    Log.e(
                        TAG,
                        "Criteria cannot be found at the selected position"
                    ); return
                }
                criteria.alternativesList = alternativesList

                this.alternativesList = alternativesList.map { alternatives ->
                    Alternatives(
                        alternatives.parentName,
                        alternatives.children.map { alternative -> Alternative(alternative.name) }.toMutableList()
                    )
                } as ArrayList<Alternatives>

                for (i in 0 until criteriaList.size) {
                    if (i == selectedCriterionPosition) continue

                    val c = criteriaList[i]
                    val list = c.alternativesList
                    c.alternativesList = ArrayList(this.alternativesList)
                    if (list == null) continue
                    val newList = c.alternativesList ?: run {
                        Log.e(
                            TAG,
                            "The alternatives list is null after assignment"
                        ); return
                    }

                    for (j in 0 until list.size) {
                        for (k in 0 until list[j].children.size) {
                            newList[j].children[k].rating = list[j].children[k].rating
                        }
                    }
                }
            }
        }
    }

    private val onClickAddCriterion = View.OnClickListener {
        val inputCriterion = EditText(this)
        inputCriterion.inputType = InputType.TYPE_CLASS_TEXT
        inputCriterion.filters = arrayOf(InputFilter.LengthFilter(CRITERION_NAME_MAX_LENGTH))
        inputCriterion.hint = getString(R.string.criterion)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.add_criterion))
        builder.setView(inputCriterion)

        builder.setPositiveButton(R.string.add) { _, _ ->
            val input = inputCriterion.text.toString().trim()
            if (input.isEmpty()) {
                Toast.makeText(this, getString(R.string.warning_no_criterion), Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            if (criteriaList.any { criterion -> criterion.parentName.equals(input, true) }) {
                Toast.makeText(this, getString(R.string.warning_criterion_exists), Toast.LENGTH_LONG).show()
                return@setPositiveButton
            }

            val criterion = Criterion(input)
            for (criteria in criteriaList) {
                criteria.children.add(Criterion(criterion.name))
            }

            criteriaList.add(Criteria(input))
            criteriaAdapter.notifyDataSetChanged()
        }

        builder.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }

        val alertDialog = builder.create()
        alertDialog.setOnShowListener {
            inputCriterion.postDelayed({
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
            }, 100)
        }

        alertDialog.show()
    }

    private val optionsViewOnClickListener = object : CriteriaAdapter.OptionsViewOnClickListener {
        override fun onClick(groupPosition: Int, childPosition: Int) {
            selectedCriterionPosition = groupPosition

            val intent = Intent(this@CriteriaActivity, AlternativesActivity::class.java)
            val criterion = criteriaList[groupPosition]
            val alternativesList = criterion.alternativesList
            if (alternativesList != null) {
                intent.putExtra(EXTRA_ALTERNATIVES, alternativesList)
            }
            startActivityForResult(intent, REQUEST_ALTERNATIVES)
        }
    }

    private val onClickButtonCalculate = View.OnClickListener {
        val criteriaSize = criteriaList.size
        if (criteriaSize < 1) {
            Toast.makeText(this, getString(R.string.warning_no_criterion), Toast.LENGTH_SHORT).show()
            return@OnClickListener
        }
        if (criteriaSize < 2) {
            Toast.makeText(this, getString(R.string.warning_need_two_criteria), Toast.LENGTH_LONG).show()
            return@OnClickListener
        }

        if (!::alternativesList.isInitialized || alternativesList.size < 1) {
            Toast.makeText(this, getString(R.string.warning_no_alternative), Toast.LENGTH_SHORT).show()
            return@OnClickListener
        }
        val alternativesSize = alternativesList.size
        if (alternativesSize < 2) {
            Toast.makeText(this, getString(R.string.warning_need_two_alternatives), Toast.LENGTH_LONG).show()
            return@OnClickListener
        }

        val ratings: Array<IntArray> = run {
            val ratings: Array<IntArray?> = arrayOfNulls(criteriaSize)
            for (i in 0 until criteriaSize) {
                val children = criteriaList[i].children
                val childrenSize = children.size
                ratings[i] = IntArray(childrenSize)

                for (j in 0 until childrenSize) {
                    ratings[i]!![j] = children[j].rating
                }
            }
            ratings.map { ints -> ints as IntArray }.toTypedArray()
        }

        val priorities: FloatArray
        val consistencyRatio: Float

        performAhp(ratings).apply {
            priorities = first
            consistencyRatio = second
        }

        // Start ResultActivity with the calculated results
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(
            ResultActivity.ALTERNATIVES,
            criteriaList.map { criteria -> criteria.parentName }.toTypedArray()
        )
        intent.putExtra(ResultActivity.PRIORITIES, priorities)
        intent.putExtra(ResultActivity.CONSISTENCY_RATIO, consistencyRatio)
        startActivity(intent)
    }
}
