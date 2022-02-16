package org.inu.localpushalarm

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import java.util.*

class SubActivity : AppCompatActivity() {

    private val firstButton: AppCompatButton by lazy() {
        findViewById(R.id.firstButton)
    }

    private val secondButton: AppCompatButton by lazy() {
        findViewById(R.id.secondButton)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)

        firstButton.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("eventId",1)
            startActivity(intent)
        }
        secondButton.setOnClickListener {
            val intent2 = Intent(this,MainActivity::class.java).apply {
                putExtra("eventId", 2)
            }
            startActivity(intent2)
        }
    }

    fun createID(): Int {
        return SimpleDateFormat("ddHHmmss", Locale.KOREA).format(Date()).toInt()
    }
}
