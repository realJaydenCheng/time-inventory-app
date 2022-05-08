package com.jayden.timeinventory

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class Timer : AppCompatActivity() {
    private var beginTime: Long = 0
    private var endTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        //
        timeReceiver = ViewChangeReceiver()
        val filter = IntentFilter()
        filter.addAction("com.jayden.timeinventory.TIME_CHENG")
        registerReceiver(timeReceiver, filter)

        // 按钮事件
        val timerBeginBtn: Button = findViewById(R.id.timer_begin_btn)
        val timerEndBtn: Button = findViewById(R.id.timer_end_btn)
        timerBeginBtn.setOnClickListener {
            val intent = Intent(this, TimerService::class.java)
            beginTime = System.currentTimeMillis()
            intent.putExtra("begin_time", beginTime)
            startService(intent)
            timerBeginBtn.isEnabled = false
            timerEndBtn.isEnabled = true
        }
        timerEndBtn.setOnClickListener {
            val serviceIntent = Intent(this, TimerService::class.java)
            stopService(serviceIntent)
            timerBeginBtn.isEnabled = true
            timerEndBtn.isEnabled = false
            val recordIntent = Intent(this, Record::class.java)
            recordIntent.putExtra("begin_time", beginTime)
            recordIntent.putExtra("end_time", endTime)
            startActivity(recordIntent)
        }
    }

    private lateinit var timeReceiver: ViewChangeReceiver
    override fun onDestroy() {
        unregisterReceiver(timeReceiver)
        super.onDestroy()
    }

    val timeHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val timerString: TextView = findViewById(R.id.timer_string)
            timerString.text = msg.obj as String
        }
    }

    inner class ViewChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val msg = Message()
            msg.obj = intent.getStringExtra("time_string")
            endTime = intent.getLongExtra("end_time", 0)
            timeHandler.sendMessage(msg)
        }

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
