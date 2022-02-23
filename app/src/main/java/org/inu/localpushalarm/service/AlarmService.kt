package org.inu.localpushalarm.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.inu.localpushalarm.R
import org.inu.localpushalarm.receiver.AlarmReceiver
import java.util.*

class AlarmService : Service(){

    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler())

        Log.d("Test", "MyService is started")

        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val manager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance().apply {
            val from = "2022-02-22 07:55:40"
            time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).parse(from)
        }
        manager.set(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
        if (Build.VERSION.SDK_INT >= 26){
            createNotificationChannel(this)
            val notification = NotificationCompat.Builder(this, AlarmReceiver.NOTIFICATION_CHANNEL_ID)
                .setContentTitle("123 번 이벤트임다")
                .setContentText("새우버거 가져가세요~!!.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()
            startForeground(7,notification)

        }

    }
    inner class ExceptionHandler : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread?, e: Throwable?) {
            // 여기에 원하는 동작 구현
            e?.printStackTrace()
            Log.d("not normal", e.toString())
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(10)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                AlarmReceiver.NOTIFICATION_CHANNEL_ID,
                AlarmReceiver.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
        }
    }
}