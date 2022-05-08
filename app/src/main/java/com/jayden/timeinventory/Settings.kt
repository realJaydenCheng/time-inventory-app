package com.jayden.timeinventory

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.Toast

class Settings : AppCompatActivity() {

    private lateinit var cateEditIntent: Intent
    private val cateItemArrayList = ArrayList<TimeInventoryDB.RecordCategory>()
    private var resumed = false

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val appDB = TimeInventoryDB(this, "appData.db", 1)
        val db = appDB.writableDatabase

        val cateCursor = db.query("category", null, null, null, null, null, null)
        val categoryListView: ListView = findViewById(R.id.cate_setting_list)
        if (cateCursor.moveToFirst()) {
            do {
                val cateItem = TimeInventoryDB.RecordCategory()
                cateItem.id = cateCursor.getInt(cateCursor.getColumnIndex("id"))
                cateItem.name = cateCursor.getString(cateCursor.getColumnIndex("name"))
                cateItemArrayList.add(cateItem)
            } while (cateCursor.moveToNext())
        }
        cateEditIntent = Intent(this, CateEditor::class.java)
        categoryListView.adapter = CateSpinnerAdapter(
            this,
            R.layout.item_cate_listview, cateItemArrayList
        )
        categoryListView.onItemClickListener = cateSelectedListener

        val addCateBtn: Button = findViewById(R.id.add_cate_btn)
        addCateBtn.setOnClickListener {
            cateEditIntent.putExtra("isNew", true)
            startActivity(cateEditIntent)
        }

        val notificationSwitch: Button = findViewById(R.id.notification_btn)
        notificationSwitch.setOnClickListener {
            val data = getSharedPreferences("config", Context.MODE_PRIVATE)
            val notificatable = data.getBoolean("notificatable", true)
            val dataEditor = data.edit()
            if (notificatable) {
                dataEditor.putBoolean("notificatable", false)
                Toast.makeText(this, "计时通知功能已关闭！", Toast.LENGTH_SHORT).show()
            } else {
                dataEditor.putBoolean("notificatable", true)
                Toast.makeText(this, "计时通知功能已开启！", Toast.LENGTH_SHORT).show()
            }
            dataEditor.apply()
        }

        val dataCleanBtn: Button = findViewById(R.id.data_clean_btn)
        dataCleanBtn.setOnClickListener {
            val intent = Intent(this, CleanDialog::class.java)
            startActivityForResult(intent, 2)
        }

        cateCursor.close()
    }

    private val cateSelectedListener: AdapterView.OnItemClickListener =
        AdapterView.OnItemClickListener { _, _, p2, _ ->
            val item = cateItemArrayList[p2]
            cateEditIntent.putExtra("itemId", item.id)
            cateEditIntent.putExtra("itemName", item.name)
            cateEditIntent.putExtra("isNew", false)
            startActivity(cateEditIntent)
        }

    override fun onResume() {
        super.onResume()
        if (resumed) {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        resumed = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            2 -> if (resultCode == RESULT_OK) {
                val appDB = TimeInventoryDB(this, "appData.db", 1)
                val db = appDB.writableDatabase
                val isClean: Boolean? = data?.getBooleanExtra("isClean", false)
                if (isClean as Boolean) {
                    db.execSQL("drop table if exists category")
                    db.execSQL("drop table if exists record")
                    appDB.onCreate(db)
                }
            }
        }
    }
}

