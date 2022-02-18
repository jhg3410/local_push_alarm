package org.inu.localpushalarm.receiver

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.inu.localpushalarm.R
import org.inu.localpushalarm.activity.MainActivity
import java.time.LocalDateTime

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "1000"
        const val NOTIFICATION_CHANNEL_NAME = "UniLetter"
    }

    lateinit var ONOFF_KEY: String


    override fun onReceive(context: Context, intent: Intent) {

        ONOFF_KEY = intent.getStringExtra("ONOFF_KEY")!!
        createNotificationChannel(context)
        notifyNotification(context)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
        }
    }

    @SuppressLint("NewApi")
    private fun notifyNotification(context: Context) {
        val resultIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("backFromAlarm", true)
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        with(NotificationManagerCompat.from(context)) {
            val build = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle("$ONOFF_KEY 번 이벤트임다")
                .setContentText("새우버거 가져가세요~!!.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)


            notify(LocalDateTime.now().nano, build.build())
        }
    }
}
