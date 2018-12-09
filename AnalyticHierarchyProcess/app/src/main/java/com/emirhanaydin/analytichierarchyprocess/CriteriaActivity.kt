package com.emirhanaydin.analytichierarchyprocess

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_criteria.*

class CriteriaActivity : AppCompatActivity() {
    private lateinit var criterionList: MutableList<Criterion>
    private lateinit var criteriaAdapter: CriteriaAdapter
    private lateinit var alternativesList: ArrayList<Alternatives>
    private var selectedCriterionPosition = -1

    companion object {
        @JvmStatic
        val TAG: String = CriteriaActivity::class.java.name
        const val CRITERION_NAME_MAX_LENGTH = 20
        const val EXTRA_ALTERNATIVES = "ExtraAlternatives"
        const val REQUEST_ALTERNATIVES = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criteria)

        criterionList = mutableListOf()
        criteriaAdapter = CriteriaAdapter(criterionList)
        criteriaAdapter.setOnItemClickListener(onClickCriterionItemListener)
        recyclerViewCriteria.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(this@CriteriaActivity)
            adapter = criteriaAdapter
        }

        buttonAddCriterion.setOnClickListener(onClickAddCriterion)
        buttonCriteriaBack.setOnClickListener { finish() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ALTERNATIVES) {
            if (resultCode == Activity.RESULT_OK) {
                val alternativesList = if (data == null) run { Log.e(TAG, "Data intent is null"); return }
                else data.getParcelableArrayListExtra<Alternatives>(EXTRA_ALTERNATIVES)

                val criterion = criterionList.getOrNull(selectedCriterionPosition) ?: run {
                    Log.e(
                        TAG,
                        "Criterion cannot be found at the selected position"
                    ); return
                }
                criterion.alternativesList = alternativesList

                this.alternativesList = alternativesList.map { alternatives ->
                    Alternatives(
                        alternatives.parent,
                        alternatives.children.map { alternative -> Alternative(alternative.name) }.toMutableList()
                    )
                } as ArrayList<Alternatives>

                for (i in 0 until criterionList.size) {
                    if (i == selectedCriterionPosition) continue

                    val c = criterionList[i]
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
                            newList[j].children[k].value = list[j].children[k].value
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
            if (criterionList.any { criterion -> criterion.name.equals(input, true) }) {
                Toast.makeText(this, getString(R.string.warning_criterion_exists), Toast.LENGTH_LONG).show()
                return@setPositiveButton
            }

            val criterion = Criterion(input)
            if (::alternativesList.isInitialized)
                criterion.alternativesList = alternativesList
            criteriaAdapter.notifyDataSetChanged()
            criterionList.add(criterion)
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

    private val onClickCriterionItemListener = object : CriteriaAdapter.ClickListener {
        override fun onItemClick(view: View, position: Int) {
            selectedCriterionPosition = position

            val intent = Intent(this@CriteriaActivity, AlternativesActivity::class.java)
            val criterion = criterionList[position]
            val alternativesList = criterion.alternativesList
            if (alternativesList != null) {
                intent.putExtra(EXTRA_ALTERNATIVES, alternativesList)
            }
            startActivityForResult(intent, REQUEST_ALTERNATIVES)
        }
    }
}
