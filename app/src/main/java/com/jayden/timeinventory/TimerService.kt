package com.jayden.timeinventory

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.util.*
import java.util.Timer


class TimerService : Service() {


    class TimerBinder : Binder() {}

    private val timerBinder = TimerBinder()
    override fun onBind(intent: Intent): IBinder {
        return timerBinder
    }

    private var beginTime: Long = 0
    private var endTime: Long = 0
    private val timer = Timer()

    private var notificatable = true
    private lateinit var manager: NotificationManager
    private val notification = NotificationCompat.Builder(
        this,
        "timer_notification"
    )

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        // 创建通知渠道
        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "timer_notification",
                "显示计时", NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }
        val data = getSharedPreferences("config", Context.MODE_PRIVATE)
        notificatable = data.getBoolean("notificatable", true)

        beginTime = intent.getLongExtra("begin_time", 0)
        timer.schedule(AppTimerTask(), 0, 1000)



        return super.onStartCommand(intent, flags, startId)
    }

    inner class AppTimerTask : TimerTask() {
        override fun run() {
            endTime = System.currentTimeMillis()
            val timeDelta = (endTime - beginTime)
            val timeStr = TimeInventoryUtil.longToTimeString(timeDelta)
            val intent = Intent("com.jayden.timeinventory.TIME_CHENG")
            intent.`package` = "com.jayden.timeinventory"
            intent.putExtra("time_string", timeStr)
            intent.putExtra("end_time", endTime)
            sendBroadcast(intent)
            if (notificatable) {
                val sendMe = notification.setContentTitle("时光清单App正在计时")
                    .setContentText(timeStr)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            resources, R.drawable.ic_launcher_background
                        )
                    )
                    .build()
                manager.notify(1, sendMe)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
}
