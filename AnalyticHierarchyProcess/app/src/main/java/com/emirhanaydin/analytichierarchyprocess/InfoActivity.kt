package com.emirhanaydin.analytichierarchyprocess

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_info.*

class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val infoItemList = listOf(
            InfoItem(getString(R.string.info_table_10), getString(R.string.info_table_11)),
            InfoItem(getString(R.string.info_table_20), getString(R.string.info_table_21)),
            InfoItem(getString(R.string.info_table_30), getString(R.string.info_table_31)),
            InfoItem(getString(R.string.info_table_40), getString(R.string.info_table_41)),
            InfoItem(getString(R.string.info_table_50), getString(R.string.info_table_51)),
            InfoItem(getString(R.string.info_table_60), getString(R.string.info_table_61)),
            InfoItem(getString(R.string.info_table_70), getString(R.string.info_table_71)),
            InfoItem(getString(R.string.info_table_80), getString(R.string.info_table_81)),
            InfoItem(getString(R.string.info_table_90), getString(R.string.info_table_91))
        )
        val infoAdapter = InfoAdapter(this, infoItemList)
        recyclerViewInfo.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@InfoActivity)
            adapter = infoAdapter
        }

        buttonInfoBack.setOnClickListener { finish() }
    }
}
