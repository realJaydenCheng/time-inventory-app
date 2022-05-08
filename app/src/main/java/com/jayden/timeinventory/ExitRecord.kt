package com.jayden.timeinventory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ExitRecord : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exit_record)

        val yesBtn: Button = findViewById(R.id.exit_yes)
        val noBtn: Button = findViewById(R.id.exit_no)
        yesBtn.setOnClickListener {
            val intent = Intent()
            intent.putExtra("isExit", true)
            setResult(RESULT_OK, intent)
            finish()
        }
        noBtn.setOnClickListener {
            val intent = Intent()
            intent.putExtra("isExit", false)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}