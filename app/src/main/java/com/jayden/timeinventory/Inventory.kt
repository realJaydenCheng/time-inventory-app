package com.jayden.timeinventory

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView

class Inventory : AppCompatActivity() {

    private val querySQL: String = "select * " +
            "from record join category " +
            "on category.id = record.cateId"

    private var recordInventoryArrayList = ArrayList<TimeInventoryDB.RecordInventory>()

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        val appDB = TimeInventoryDB(this, "appData.db", 1)
        val db = appDB.readableDatabase

        val inventoryCursor = db.rawQuery(querySQL, null)
        val inventoryListView: ListView = findViewById(R.id.inventor_list)
        if (inventoryCursor.moveToFirst()) {
            do {
                val recordItem = TimeInventoryDB.RecordInventory()
                recordItem.reId =
                    inventoryCursor.getInt(inventoryCursor.getColumnIndex("record.id"))
                recordItem.beginTime =
                    inventoryCursor.getLong(inventoryCursor.getColumnIndex("record.beginTime"))
                recordItem.cateId =
                    inventoryCursor.getInt(inventoryCursor.getColumnIndex("record.cateId"))
                recordItem.cateName =
                    inventoryCursor.getString(inventoryCursor.getColumnIndex("category.name"))
                recordItem.endTime =
                    inventoryCursor.getLong(inventoryCursor.getColumnIndex("record.endTime"))
                recordItem.reName =
                    inventoryCursor.getString(inventoryCursor.getColumnIndex("record.title"))
                recordInventoryArrayList.add(recordItem)
            } while (inventoryCursor.moveToNext())
        }
        inventoryListView.adapter = InventoryListAdapter(
            this,
            R.layout.item_record_list, recordInventoryArrayList
        )

        inventoryCursor.close()
    }
}

class InventoryListAdapter(
    activity: Activity, val resId: Int,
    data: List<TimeInventoryDB.RecordInventory>
) : ArrayAdapter<TimeInventoryDB.RecordInventory>(activity, resId, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(resId, parent, false)

        val itemNameView = view.findViewById<View>(R.id.item_record_name) as TextView
        val itemCateView = view.findViewById<View>(R.id.item_cate_name) as TextView
        val itemTimeView = view.findViewById<View>(R.id.item_time_delta) as TextView
        val itemBeginView = view.findViewById<View>(R.id.item_begin) as TextView
        val itemEndView = view.findViewById<View>(R.id.item_end) as TextView

        val item = getItem(position)

        itemNameView.text = item?.reName
        itemCateView.text = item?.cateName
        itemBeginView.text = TimeInventoryUtil.longToDateTimeString(item?.beginTime as Long)
        itemEndView.text = TimeInventoryUtil.longToDateTimeString(item?.endTime as Long)
        itemTimeView.text = TimeInventoryUtil.longToTimeString(
            item?.endTime as Long - item?.beginTime as Long
        )

        return view
    }
}