package com.emirhanaydin.analytichierarchyprocess

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_result.*
import java.text.NumberFormat

class ResultActivity : AppCompatActivity() {
    companion object {
        const val ALTERNATIVES = "Alternatives"
        const val PRIORITIES = "Priorities"
        const val CONSISTENCY_RATIO = "ConsistencyRatio"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val alternatives = intent.getStringArrayExtra(ALTERNATIVES)
        val priorities = intent.getFloatArrayExtra(PRIORITIES)
        val consistencyRatio = intent.getFloatExtra(CONSISTENCY_RATIO, 0f)

        val priorityFormat = NumberFormat.getPercentInstance().apply {
            minimumFractionDigits = 2
        }
        textViewConsistencyRatio.text = priorityFormat.format(consistencyRatio)

        if (consistencyRatio < 0.1f) {
            textViewConsistency.text = getString(R.string.consistent)
            textViewConsistency.setTextColor(Color.GREEN)
        } else {
            textViewConsistency.text = getString(R.string.not_consistent)
            textViewConsistency.setTextColor(Color.RED)
        }

        val criteriaPrioritiesList: MutableList<AlternativePriority> = mutableListOf()
        for (i in 0 until alternatives.size) {
            criteriaPrioritiesList.add(AlternativePriority(alternatives[i], priorities[i]))
        }

        val resultAdapter = ResultAdapter(criteriaPrioritiesList)
        val recyclerViewResult: RecyclerView = findViewById<RecyclerView>(R.id.recycler_view_result).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@ResultActivity)
            adapter = resultAdapter
        }

        recyclerViewResult.adapter = resultAdapter
    }
}
