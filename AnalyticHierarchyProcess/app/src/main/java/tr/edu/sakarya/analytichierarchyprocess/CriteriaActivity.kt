package tr.edu.sakarya.analytichierarchyprocess

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_criteria.*

class CriteriaActivity : AppCompatActivity() {
    private lateinit var criteriaList: MutableList<Criteria>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criteria)

        criteriaList = mutableListOf()
        val criteriaAdapter = CriteriaAdapter(this, R.layout.layout_criteria, criteriaList)
        listViewCriteria.adapter = criteriaAdapter

        buttonAddCriteria.setOnClickListener {
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.VERTICAL

            val inputCriteria = EditText(this)
            inputCriteria.inputType = InputType.TYPE_CLASS_TEXT
            inputCriteria.filters = arrayOf(InputFilter.LengthFilter(CRITERIA_NAME_MAX_LENGTH))
            inputCriteria.hint = getString(R.string.criteria)

            val inputValue = EditText(this)
            inputValue.inputType = InputType.TYPE_CLASS_NUMBER
            inputValue.hint = getString(R.string.value)

            linearLayout.addView(inputCriteria)
            linearLayout.addView(inputValue)

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.add_criteria))
            builder.setView(linearLayout)

            builder.setPositiveButton(getString(R.string.add)) { _, _ ->
                val criteriaInput = inputCriteria.text.toString()
                val valueInput = inputValue.text.toString().toInt()
                val criteria = Criteria(criteriaInput, valueInput)
                criteriaList.add(criteria)

                criteriaAdapter.notifyDataSetChanged()
            }
            builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }

            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }
    }
}
