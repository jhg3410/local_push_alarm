package org.inu.localpushalarm.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import org.inu.localpushalarm.R
import org.inu.localpushalarm.util.SharedPreferenceWrapper
import org.json.JSONArray
import java.util.*

class SubActivity : AppCompatActivity() {

    private val firstButton: AppCompatButton by lazy() {
        findViewById(R.id.firstButton)
    }

    private val secondButton: AppCompatButton by lazy() {
        findViewById(R.id.secondButton)
    }

    private lateinit var sharedPreferences: SharedPreferences
    private var getSharedDate: String? = ""
    private var getSharedId: String? = ""
    private var dateArr: ArrayList<String> = ArrayList()
    private var idArr: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)
        val db = SharedPreferenceWrapper(this)

        firstButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fromSub", 1)
            startActivity(intent)
        }
        secondButton.setOnClickListener {
            val intent2 = Intent(this, MainActivity::class.java).apply {
                putExtra("fromSub", 2)
            }

            startActivity(intent2)
        }
    }
}
