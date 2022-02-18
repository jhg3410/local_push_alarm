package org.inu.localpushalarm.activity

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import org.inu.localpushalarm.AlarmDialog
import org.inu.localpushalarm.R
import org.inu.localpushalarm.databinding.ActivityMainBinding
import org.inu.localpushalarm.model.AlarmDisplayModel
import org.inu.localpushalarm.observe
import org.inu.localpushalarm.receiver.AlarmReceiver
import org.inu.localpushalarm.viewmodel.MainViewModel
import java.util.*


class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    // 각 이벤트마다 다르게 SharedPreferences 키를 다르게 하기 위해
    private var ONOFF_KEY: String = (-1).toString()

    private val dialog = AlarmDialog()
    private var backFromAlarm = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // step0 뷰 초기화
        initBinding()
        getIntentValue()
        initOnOffButton()

        // step1 데이터 가져오기
        val model = fetchDataFromSharedPreferences()

        // step2 뷰에 데이터 그려주기
        renderView(model)
    }

    private fun getIntentValue() {
        ONOFF_KEY = intent.getIntExtra("eventId", -1).toString()
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.mainViewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun initOnOffButton() {
        observe(viewModel.alarmClickEvent) {
            // Dialog Floating!
            val model = binding.onOffButton.tag as? AlarmDisplayModel ?: return@observe
            val newModel = saveAlarmModel(model.onOff.not())
            renderView(newModel)
            if (newModel.onOff) {
                dialog.showDialog(this,"알림신청","알림 신청이 완료 되었습니다\n" +
                        "행사 5분전에 푸쉬 드릴게요 :)")
                // On -> 알람 등록
                val calendar = Calendar.getInstance().apply {
                    val from =
                        if (ONOFF_KEY == "1") "2022-02-19 05:08:40" else "2022-02-20 02:06:00"
                    time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).parse(from)
                }
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(this, AlarmReceiver::class.java)
                intent.putExtra("ONOFF_KEY", binding.ONOFFKEY.text)
                val pendingIntent = PendingIntent.getBroadcast(
                    // 이렇게하면 계속 쌓이기에 ONOFF_KEY 로 하면 각 eventId에 맞게 업데이트
                    this, ONOFF_KEY.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT
                )

                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                dialog.showDialog(this,"알림취소","알림을 정말 취소하시겠어요?\n" +
                        "ㅠ0ㅠ")
                // Off -> 알람 제거
                cancelAlarm()
            }
        }
    }

    private fun saveAlarmModel(onOff: Boolean): AlarmDisplayModel {
        val model = AlarmDisplayModel(
            onOff = onOff
        )
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean(ONOFF_KEY, model.onOff)
            commit()
        }
        return model
    }

    private fun fetchDataFromSharedPreferences(): AlarmDisplayModel {
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val onOffDBValue = sharedPreferences.getBoolean(ONOFF_KEY, false)
        val alarmModel = AlarmDisplayModel(
            onOff = onOffDBValue
        )
//         보정 예외처리
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            ONOFF_KEY.toInt(),
            Intent(this, AlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE
        )

        if ((pendingIntent == null) and alarmModel.onOff) {
            // 알람은 꺼져있는데 데이터는 켜져있는 경우
            alarmModel.onOff = false
        } else if ((pendingIntent != null) and alarmModel.onOff.not()) {
            // 알람은 커져있는데 데이터는 꺼져있는 경우
            // 알람 취소
            pendingIntent.cancel()
        }
        return alarmModel
    }

    private fun renderView(model: AlarmDisplayModel) {
        viewModel.loadOnOffButton(onOff = model.onOff)
        binding.onOffButton.tag = model

        // 어느 이벤트에서 온지 확인을 위한 구문
        binding.ONOFFKEY.text = ONOFF_KEY
    }

    private fun cancelAlarm() {
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            ONOFF_KEY.toInt(),
            Intent(this, AlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE
        )
        pendingIntent?.cancel()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backFromAlarm = intent.getBooleanExtra("backFromAlarm",false)
        Log.d("????",backFromAlarm.toString())
        if (backFromAlarm) {

            startActivity(Intent(this,SubActivity::class.java))
            finish()
        }
    }

    companion object {
        private const val SHARED_PREFERENCES_NAME = "alarm"
    }
}