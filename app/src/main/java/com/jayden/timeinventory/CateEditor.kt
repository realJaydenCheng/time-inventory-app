package com.jayden.timeinventory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class CateEditor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cate_editor)

        val appDB = TimeInventoryDB(this, "appData.db", 1)
        val db = appDB.writableDatabase

        val isNew = intent.getBooleanExtra("isNew", false)
        val titleTextView: TextView = findViewById(R.id.editor_title)
        val deleteBtn: Button = findViewById(R.id.editor_delete)
        if (isNew) {
            titleTextView.text = "新建分类"
            deleteBtn.visibility = View.GONE
        } else {
            titleTextView.text = "编辑分类"
            val nameEditor: EditText = findViewById(R.id.cate_text_editor)
            nameEditor.setText(intent.getStringExtra("itemName"))
        }

        val saveBtn: Button = findViewById(R.id.editor_save)
        val cancelBtn: Button = findViewById(R.id.editor_cancel)
        cancelBtn.setOnClickListener {
            finish()
        }
        saveBtn.setOnClickListener {
            val itemId = intent.getIntExtra("itemId", 0)
            val deleteSQL = "delete from category where id = $itemId"
            val itemName = findViewById<EditText>(R.id.cate_text_editor).text.toString()
            var insertSQL = "insert into category (id,name) values ($itemId, '$itemName')"
            if (isNew) {
                insertSQL = "insert into category (id,name) values (null, '$itemName')"
            }
            db.execSQL(deleteSQL)
            db.execSQL(insertSQL)
            finish()
        }
        deleteBtn.setOnClickListener {
            val itemId = intent.getIntExtra("itemId", 0)
            val delReSQL = "delete from record where cateId = $itemId"
            val deleteSQL = "delete from category where id = $itemId"
            db.execSQL(delReSQL)
            db.execSQL(deleteSQL)
            finish()
        }
    }
}