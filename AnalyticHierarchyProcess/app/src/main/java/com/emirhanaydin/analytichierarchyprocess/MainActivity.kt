package com.emirhanaydin.analytichierarchyprocess

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonHelp = findViewById<Button>(R.id.buttonHelp)
        buttonHelp.setOnClickListener {
            val intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
        }

        val buttonNewAnalyze = findViewById<Button>(R.id.buttonNewAnalyze)
        buttonNewAnalyze.setOnClickListener {
            val intent = Intent(this, AlternativesActivity::class.java)
            startActivity(intent)
        }
    }
}
