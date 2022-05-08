package com.jayden.timeinventory

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化 UI 界面
        val timerBtn: Button = findViewById(R.id.timer_btn)
        val inventorBtn: Button = findViewById(R.id.inventor_btn)
        val statisticsBtn: Button = findViewById(R.id.statistics_btn)
        val settingsBtn: Button = findViewById(R.id.settings_btn)

        timerBtn.setOnClickListener {
            val intent = Intent(this, Timer::class.java)
            startActivity(intent)
        }
        inventorBtn.setOnClickListener {
            val intent = Intent(this, Inventory::class.java)
            startActivity(intent)
        }
        statisticsBtn.setOnClickListener {
            val intent = Intent(this, Statistics::class.java)
            startActivity(intent)
        }
        settingsBtn.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }

        // SQLite 数据库存储
        val appDB = TimeInventoryDB(this, "appData.db", 1)
        appDB.writableDatabase
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.notification_switch -> {
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
            R.id.data_cleaner -> {
                val intent = Intent(this, CleanDialog::class.java)
                startActivityForResult(intent, 2)
            }
        }
        return true
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


class TimeInventoryDB(context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {

    private val createRecordSql = "create table record(" +
            "id integer primary key autoincrement," +
            "title text default '(无标题)'," +
            "beginTime long not null," +
            "endTime long not null," +
            "cateId integer default 0)"
    private val createCategorySql = "create table category(" +
            "id integer primary key autoincrement," +
            "name text)"
    private val defaultCategorySQL = "insert into category values(0, '默认')"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createRecordSql)
        db?.execSQL(createCategorySql)
        db?.execSQL(defaultCategorySQL)
    }

    class RecordCategory {
        var id: Int = 0
        lateinit var name: String
    }

    class RecordInventory {
        var reId: Int = 0
        var beginTime: Long = 0
        var endTime: Long = 0
        lateinit var reName: String
        var cateId: Int = 0
        lateinit var cateName: String
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}

object TimeInventoryUtil {
    fun longToTimeString(timeLong: Long): String {
        val time = timeLong / 1000
        val hour = time / 3600
        val minute = (time % 3600) / 60
        val second = time % 60
        return String.format("%02d : %02d : %02d", hour, minute, second)
    }

    fun longToDateTimeString(timeLong: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.format(timeLong)
    }
}