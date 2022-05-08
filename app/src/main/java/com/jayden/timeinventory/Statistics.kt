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

class Statistics : AppCompatActivity() {
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        val cateListView: ListView = findViewById(R.id.cate_statistics)
        val appDB = TimeInventoryDB(this, "appData.db", 1)
        val db = appDB.readableDatabase

        val cateSQL = "select sum(endTime-beginTime) as time, name " +
                "from record join category " +
                "on cateId = category.id group by cateId"
        val cateCursor = db.rawQuery(cateSQL, null)

        val staArrayList = ArrayList<CateSta>()
        if (cateCursor.moveToFirst()) {
            do {
                val cateItem = CateSta(
                    cateCursor.getLong(cateCursor.getColumnIndex("time")),
                    cateCursor.getString(cateCursor.getColumnIndex("name"))
                )
                staArrayList.add(cateItem)
            } while (cateCursor.moveToNext())
        }
        cateCursor.close()
        cateListView.adapter = CateListAdapter(
            this,
            R.layout.item_statistics_listview, staArrayList
        )

    }

    class CateSta(val time: Long, val name: String)

    class CateListAdapter(
        activity: Activity, val resId: Int,
        data: List<CateSta>
    ) : ArrayAdapter<CateSta>(activity, resId, data) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = LayoutInflater.from(context).inflate(resId, parent, false)
            val itemTimeView = view.findViewById<View>(R.id.sum_time_length) as TextView
            val itemCateView = view.findViewById<View>(R.id.statistics_item_name) as TextView
            val item = getItem(position)
            itemCateView.text = item?.name
            itemTimeView.text = TimeInventoryUtil.longToTimeString(item?.time as Long)
            return view
        }
    }
}