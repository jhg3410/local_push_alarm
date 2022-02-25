package org.inu.localpushalarm.receiver

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.inu.localpushalarm.R
import org.inu.localpushalarm.activity.MainActivity
import org.inu.localpushalarm.service.BootService
import org.inu.localpushalarm.util.SharedPreferenceWrapper
import org.json.JSONArray
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "1000"
        const val NOTIFICATION_CHANNEL_NAME = "UniLetter"
    }

    private var eventId: Int = -1

    override fun onReceive(context: Context, intent: Intent) {
        val db = SharedPreferenceWrapper(context)
        val dateArr = db.getArrayString("date") ?: arrayOf()
        val idArr = db.getArrayInt("id") ?: arrayOf()


        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            for (idx in idArr.indices) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val alarmIntent = Intent(context, AlarmReceiver::class.java)
                alarmIntent.putExtra("eventID", idArr[idx])
                val pendingIntent = PendingIntent.getBroadcast(
                    // 이렇게하면 계속 쌓이기에 ONOFF_KEY 로 하면 각 eventId에 맞게 업데이트
                    context, idArr[idx], alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT
                )
                val calendar = Calendar.getInstance().apply {
                    // todo 시간 알맞게
                    val from = dateArr[idx]
                    time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).parse(from)
                }
                alarmManager.setExactAndAllowWhileIdle(  //  절전모드일 때도 울리게 아니면 .setExact
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
            return
        }

        context.startService(Intent(context,BootService::class.java))
        context.stopService(Intent(context,BootService::class.java))
        getIntentValue(intent)
        createNotificationChannel(context)
        notifyNotification(context)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventId,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE
        )
        pendingIntent?.cancel()
    }
    private fun getIntentValue(intent: Intent) {
        eventId = intent.getIntExtra("eventID",-1)
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
                .setContentTitle("$eventId 번 이벤트임다")
                .setContentText("새우버거 가져가세요~!!.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            notify(LocalDateTime.now().nano, build.build())
        }
    }
}
