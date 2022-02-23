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
import org.inu.localpushalarm.util.AlarmForm
import org.inu.localpushalarm.util.SharedPreferenceWrapper
import org.inu.localpushalarm.viewmodel.MainViewModel
import org.json.JSONArray
import java.util.*


class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    // 각 이벤트마다 다르게 SharedPreferences 키를 다르게 하기 위해
    private var eventID: Int = -1

//    var dateArr : ArrayList<String> = ArrayList()
//    var idArr : ArrayList<String> = ArrayList()
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
        eventID = intent.getIntExtra("fromSub", -1)
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
                AlarmForm(this, eventID = eventID).registerAlarmInMain("2022-02-23 09:12:40")
            } else {
                dialog.showDialog(this,"알림취소","알림을 정말 취소하시겠어요?\n" +
                        "ㅠ0ㅠ")
                // Off -> 알람 제거
                AlarmForm(this, eventID = eventID).cancelAlarm()
            }
        }
    }

    private fun saveAlarmModel(onOff: Boolean): AlarmDisplayModel {
        val model = AlarmDisplayModel(
            onOff = onOff
        )
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean(eventID.toString(), model.onOff)
            commit()
        }
        return model
    }

    private fun fetchDataFromSharedPreferences(): AlarmDisplayModel {
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val onOffDBValue = sharedPreferences.getBoolean(eventID.toString(), false)
        val alarmModel = AlarmDisplayModel(
            onOff = onOffDBValue
        )
        //  보정 예외처리
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            eventID.toInt(),
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
        binding.ONOFFKEY.text = eventID.toString()
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