package com.emirhanaydin.analytichierarchyprocess

import android.app.Activity
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alternatives)

        val extras = intent.extras
        alternativesList = extras
            ?.getParcelableArrayList<Alternatives>(CriteriaActivity.EXTRA_ALTERNATIVES)
            ?.toMutableList()
                ?: mutableListOf()

        alternativesAdapter = AlternativesAdapter(this, alternativesList)
        expandableListViewAlternatives.setAdapter(alternativesAdapter)

        buttonAddAlternative.setOnClickListener(onClickAddAlternative)
        buttonAlternativesSave.setOnClickListener(onClickButtonSave)
        buttonAlternativesBack.setOnClickListener(onClickButtonBack)
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

    private val onClickButtonSave = View.OnClickListener {
        setResult(
            Activity.RESULT_OK,
            Intent().putParcelableArrayListExtra(CriteriaActivity.EXTRA_ALTERNATIVES, ArrayList(alternativesList))
        )
        finish()
    }

    private val onClickButtonBack = View.OnClickListener {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
