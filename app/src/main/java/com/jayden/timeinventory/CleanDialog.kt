package com.jayden.timeinventory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class CleanDialog : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clean_dialog)

        val yesBtn: Button = findViewById(R.id.clean_yes)
        val noBtn: Button = findViewById(R.id.clean_no)
        yesBtn.setOnClickListener {
            val intent = Intent()
            intent.putExtra("isClean", true)
            setResult(RESULT_OK, intent)
            finish()
        }
        noBtn.setOnClickListener {
            val intent = Intent()
            intent.putExtra("isClean", false)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}