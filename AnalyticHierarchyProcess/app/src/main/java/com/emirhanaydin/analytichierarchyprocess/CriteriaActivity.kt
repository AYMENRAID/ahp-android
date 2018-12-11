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
    private lateinit var alternativesList: List<Alternatives>
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
        buttonCriteriaExit.setOnClickListener { finish() }
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
                if (this::alternativesList.isInitialized && alternativesList == this.alternativesList) return

                setAlternativesList(alternativesList)
            }
        }
    }

    private fun getNewAlternativesList(alternativesList: List<Alternatives>): List<Alternatives> {
        return alternativesList.map { alternatives ->
            Alternatives(
                alternatives.parentName,
                alternatives.children.map { alternative -> Alternative(alternative.name) }.toMutableList()
            )
        }
    }

    private fun setCriteriaAlternativesList(criteria: Criteria, alternativesList: List<Alternatives>) {
        val list = criteria.alternativesList
        val newList = getNewAlternativesList(alternativesList) as ArrayList
        criteria.alternativesList = newList
        if (list == null) return

        for (j in 0 until list.size) {
            for (k in 0 until list[j].children.size) {
                newList[j].children[k].rating = list[j].children[k].rating
            }
        }
    }

    private fun setAlternativesList(alternativesList: List<Alternatives>) {
        this.alternativesList = getNewAlternativesList(alternativesList) as ArrayList<Alternatives>

        for (i in 0 until criteriaList.size) {
            if (i == selectedCriterionPosition) continue

            val criteria = criteriaList[i]
            setCriteriaAlternativesList(criteria, alternativesList)
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

            val criteria = Criteria(input)
            if (this::alternativesList.isInitialized)
                setCriteriaAlternativesList(criteria, this.alternativesList)
            criteriaList.add(criteria)
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

        if (!::alternativesList.isInitialized || alternativesList.isEmpty()) {
            Toast.makeText(this, getString(R.string.warning_no_alternative), Toast.LENGTH_SHORT).show()
            return@OnClickListener
        }
        val alternativesSize = alternativesList.size
        if (alternativesSize < 2) {
            Toast.makeText(this, getString(R.string.warning_need_two_alternatives), Toast.LENGTH_LONG).show()
            return@OnClickListener
        }

        val criteriaRatings = getRatingsArray(criteriaList)
        val criteriaPriorities: FloatArray
        val criteriaConsistencyIndex: Float
        val criteriaRandomIndex: Float

        performAhp(criteriaRatings).apply {
            criteriaPriorities = this.priorities
            criteriaConsistencyIndex = this.consistencyIndex
            criteriaRandomIndex = this.randomIndex
        }

        var alternativeRatings = arrayOf<Array<FloatArray>>()
        var alternativePriorities = arrayOf<FloatArray>()
        var alternativeConsistencyIndexes = arrayOf<Float>()
        var alternativeRandomIndex = arrayOf<Float>()
        var alternativeConsistencyRatios = arrayOf<Float>()

        for (i in 0 until criteriaSize) {
            alternativeRatings += getRatingsArray(criteriaList[i].alternativesList as List<AhpGroup>)
            performAhp(alternativeRatings[i]).apply {
                alternativePriorities += this.priorities
                alternativeConsistencyIndexes += this.consistencyIndex
                alternativeRandomIndex += this.consistencyRatio
                alternativeConsistencyRatios += this.consistencyRatio
            }
        }

        val priorities = FloatArray(alternativesSize) { 0f }
        var consistencyIndex = criteriaConsistencyIndex
        var randomIndex = criteriaRandomIndex

        for (i in 0 until criteriaSize) {
            // Add the weight of the criterion on all consistency and random indexes to their total weight
            consistencyIndex += criteriaPriorities[i] * alternativeConsistencyIndexes[i]
            randomIndex += criteriaPriorities[i] * alternativeRandomIndex[i]
            for (j in 0 until alternativesSize) {
                // Add the weight of the criterion on each alternative to its total weight
                priorities[j] += criteriaPriorities[i] * alternativePriorities[i][j]
            }
        }
        val consistencyRatio = consistencyIndex / randomIndex

        // Start ResultActivity with the calculated results
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(
            ResultActivity.ALTERNATIVES,
            alternativesList.map { alternatives -> alternatives.parentName }.toTypedArray()
        )
        intent.putExtra(ResultActivity.PRIORITIES, priorities)
        intent.putExtra(ResultActivity.CONSISTENCY_RATIO, consistencyRatio)
        startActivity(intent)
    }

    private fun getRatingsArray(ahpGroupList: List<AhpGroup>): Array<FloatArray> {
        val size = ahpGroupList.size
        val ratings: Array<FloatArray?> = arrayOfNulls(size)

        for (i in 0 until size) {
            val children = ahpGroupList[i].children
            val childrenSize = children.size
            ratings[i] = FloatArray(childrenSize)

            for (j in 0 until childrenSize) {
                val rating = children[j].rating.toFloat()
                ratings[i]!![j] = if (children[j].isReciprocal) 1f / rating else rating
            }
        }

        return ratings.filterIsInstance<FloatArray>().toTypedArray()
    }
}
