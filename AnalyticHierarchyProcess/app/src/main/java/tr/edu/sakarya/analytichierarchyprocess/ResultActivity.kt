package tr.edu.sakarya.analytichierarchyprocess

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class ResultActivity : AppCompatActivity() {
    companion object {
        const val CRITERIA = "Criteria"
        const val PRIORITIES = "Priorities"
        const val CONSISTENCY_RATIO = "ConsistencyRatio"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val criteria = intent.getStringArrayExtra(CRITERIA)
        val priorities = intent.getFloatArrayExtra(PRIORITIES)
        // TODO val consistencyRatio = intent.getFloatExtra(CONSISTENCY_RATIO, 0f)

        val criteriaPrioritiesList: MutableList<CriterionPriority> = mutableListOf()
        for (i in 0 until criteria.size) {
            criteriaPrioritiesList.add(CriterionPriority(criteria[i], priorities[i]))
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
