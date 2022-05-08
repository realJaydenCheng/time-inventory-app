@file:Suppress("DEPRECATION")

package com.jayden.timeinventory

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class Record : AppCompatActivity() {
    private var beginTime: Long = 0
    private var endTime: Long = 0
    private var cateId: Int = 0
    private var cateItemArrayList = ArrayList<TimeInventoryDB.RecordCategory>()

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        val appDB = TimeInventoryDB(this, "appData.db", 1)
        val db = appDB.writableDatabase

        beginTime = intent.getLongExtra("begin_time", 0)
        endTime = intent.getLongExtra("end_time", 0)
        val beginTimeView: TextView = findViewById(R.id.beginTimeView)
        val endTimeView: TextView = findViewById(R.id.endTimeView)
        val timeLengthView: TextView = findViewById(R.id.timeLengthView)
        beginTimeView.text = "开始时间：" + TimeInventoryUtil.longToDateTimeString(beginTime)
        endTimeView.text = "结束时间：" + TimeInventoryUtil.longToDateTimeString(endTime)
        timeLengthView.text = TimeInventoryUtil.longToTimeString(endTime - beginTime)

        val cateCursor = db.query("category", null, null, null, null, null, null)
        val categorySpinner: Spinner = findViewById(R.id.categorySpinner)
        if (cateCursor.moveToFirst()) {
            do {
                val cateItem = TimeInventoryDB.RecordCategory()
                cateItem.id = cateCursor.getInt(cateCursor.getColumnIndex("id"))
                cateItem.name = cateCursor.getString(cateCursor.getColumnIndex("name"))
                cateItemArrayList.add(cateItem)
            } while (cateCursor.moveToNext())
        }
        categorySpinner.adapter = CateSpinnerAdapter(
            this,
            R.layout.item_cate_spinner, cateItemArrayList
        )
        categorySpinner.onItemSelectedListener = cateSelectedListener


        val saveBtn: Button = findViewById(R.id.saveBtn)
        val cancelBtn: Button = findViewById(R.id.cancelBtn)
        saveBtn.setOnClickListener {
            val nameTextView: EditText = findViewById(R.id.editTexRecordName)
            val nameStr = nameTextView.text.toString()
            val savingValues = ContentValues().apply {
                put("beginTime", beginTime)
                put("title", nameStr)
                put("endTime", endTime)
                put("cateId", cateId)
            }
            db.insert("record", null, savingValues)
            finish()
        }
        cancelBtn.setOnClickListener {
            val exitRecordIntent = Intent(this, ExitRecord::class.java)
            startActivityForResult(exitRecordIntent, 1)
        }
        cateCursor.close()
    }

    override fun onBackPressed() {
        val exitRecordIntent = Intent(this, ExitRecord::class.java)
        startActivityForResult(exitRecordIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> if (resultCode == RESULT_OK) {
                val isExit: Boolean? = data?.getBooleanExtra("isExit", false)
                if (isExit as Boolean) {
                    finish()
                }
            }
        }
    }

    private val cateSelectedListener: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?,
                position: Int, id: Long
            ) {
                val selectedCateItem = cateItemArrayList[position]
                cateId = selectedCateItem.id
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

}

class CateSpinnerAdapter(
    activity: Activity, val resId: Int,
    data: List<TimeInventoryDB.RecordCategory>
) : ArrayAdapter<TimeInventoryDB.RecordCategory>(activity, resId, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(resId, parent, false)
        val itemTextView = view.findViewById<View>(R.id.item_text) as TextView
        val itemIdView = view.findViewById<View>(R.id.item_id) as TextView
        val item = getItem(position)
        itemTextView.text = item?.name
        itemIdView.text = item?.id.toString()
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(resId, parent, false)
        val itemTextView = view.findViewById<View>(R.id.item_text) as TextView
        val itemIdView = view.findViewById<View>(R.id.item_id) as TextView
        val item = getItem(position)
        itemTextView.text = item?.name
        itemIdView.text = item?.id.toString()
        return view
    }
}