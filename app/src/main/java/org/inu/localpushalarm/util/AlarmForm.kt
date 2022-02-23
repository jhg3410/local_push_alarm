package org.inu.localpushalarm.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import org.inu.localpushalarm.receiver.AlarmReceiver
import java.util.*

class AlarmForm(val context: Context, private val eventID:Int){

    fun registerAlarmInMain(date:String){
        val calendar = Calendar.getInstance().apply {
            time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).parse(date)
        }
        val db = SharedPreferenceWrapper(context)

        val dateArrayList = addArrayList("date",date)

        val idArrayList = addArrayList("id",eventID)

        db.putArrayString("date",dateArrayList.toTypedArray())
        db.putArrayInt("id",idArrayList.toTypedArray())

        Log.d("dsp,vpos,veopi,voie,mofim,eoifm,oiemfefomew","$dateArrayList $idArrayList")


        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("eventID", eventID)
        val pendingIntent = PendingIntent.getBroadcast(
            // 이렇게하면 계속 쌓이기에 ONOFF_KEY 로 하면 각 eventId에 맞게 업데이트
            context, eventID, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    fun cancelAlarm() {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventID,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE
        )
        pendingIntent?.cancel()

        //shared 삭제

        val db = SharedPreferenceWrapper(context)

        val idArray = db.getArrayInt("id")
        val idArrayList = arrayListOf<Int>()
        idArray?.let{
            for (id in it)
                idArrayList.add(id)
        }
        val idx = idArrayList.indexOf(eventID)
        idArrayList.removeAt(idx)

        val dateArray = db.getArrayString("date")
        val dateArrayList = arrayListOf<String>()
        dateArray?.let {
            for (date in it)
                dateArrayList.add(date)
        }
        dateArrayList.removeAt(idx)

        Log.d("dsp,vpos,veopi,voie,mofim,eoifm,oiemfefomew","$dateArrayList $idArrayList")

        db.putArrayString("date",dateArrayList.toTypedArray())
        db.putArrayInt("id",idArrayList.toTypedArray())
    }

    inline fun <reified T> addArrayList(key: String, value:T): ArrayList<T> {
        val db = SharedPreferenceWrapper(context)
        lateinit var array: Array<T>
        if (key == "id" ){
            array = db.getArrayInt("id")?.let {
                it as Array<T>
            } ?: arrayOf()
        }
        else{
            array = db.getArrayString("date")?.let {
                it as Array<T>
            } ?: arrayOf()
        }

        val arrayList = arrayListOf<T>()
        array.let{
            for (id in it)
                arrayList.add(id)
        }
        arrayList.add(value)
        return arrayList
    }
}