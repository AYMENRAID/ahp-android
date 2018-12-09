package com.emirhanaydin.analytichierarchyprocess

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_criteria.*

class CriteriaActivity : AppCompatActivity() {
    private lateinit var criterionList: MutableList<Criterion>
    private lateinit var criteriaAdapter: CriteriaAdapter

    companion object {
        const val CRITERION_NAME_MAX_LENGTH = 20
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
            criterionList.add(criterion)
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

    private val onClickCriterionItemListener = object : CriteriaAdapter.ClickListener {
        override fun onItemClick(view: View, position: Int) {
            val intent = Intent(this@CriteriaActivity, AlternativesActivity::class.java)
            intent.putExtra(AlternativesActivity.EXTRA_CRITERION, criterionList[position])
            startActivity(intent)
        }
    }
}
