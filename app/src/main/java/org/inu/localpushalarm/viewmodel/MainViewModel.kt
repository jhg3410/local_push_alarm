package org.inu.localpushalarm.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.inu.localpushalarm.R
import org.inu.localpushalarm.util.SingleLiveEvent

class MainViewModel: ViewModel() {
    val alarmClickEvent = SingleLiveEvent<Any>()
    val onOffText = MutableLiveData<String>()
    val onOffColor = MutableLiveData<Int>()
    val onOffBackground = MutableLiveData<Int>()


    // 알람버튼 클릭했을 때 이벤트
    fun onClickButton(){
        alarmClickEvent.call()
    }

    fun loadOnOffButton(onOff:Boolean){
        onOffText.value = if (onOff) "알람 취소하기" else "알람 받기"   // alarmOnOff textView(text)
        onOffColor.value = if (onOff) R.color.primary else R.color.background   // alarmOnOff textView(textColor)
        onOffBackground.value = if (onOff) R.color.background else R.color.primary  // alarmOnOff textView(background)
    }
}