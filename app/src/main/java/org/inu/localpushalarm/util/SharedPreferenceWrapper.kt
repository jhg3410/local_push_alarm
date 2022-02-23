package org.inu.localpushalarm.util

import android.content.Context
import android.preference.PreferenceManager
import android.text.TextUtils
import androidx.core.content.edit

class SharedPreferenceWrapper(context: Context) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun putArrayString(key: String, arrayString: Array<String>) {
        preferences.edit {
            putString(key, TextUtils.join("‚‗‚", arrayString))
        }
    }

    fun getArrayString(key: String): Array<String>? {
        val record = preferences.getString(key, null) ?: return null

        return TextUtils.split(record, "‚‗‚").map { it }.toTypedArray()
    }

    fun putArrayBoolean(key: String, arrayBoolean: Array<Boolean>) {
        preferences.edit {
            putString(key, TextUtils.join("‚‗‚", arrayBoolean))
        }
    }

    fun getArrayBoolean(key: String): Array<Boolean>? {
        val record = preferences.getString(key, null) ?: return null

        return TextUtils.split(record, "‚‗‚").map { it.toBoolean() }.toTypedArray()
    }

    fun putArrayInt(key: String, arrayInt: Array<Int>) {
        preferences.edit {
            putString(key, TextUtils.join("‚‗‚", arrayInt))
        }
    }

    fun getArrayInt(key: String): Array<Int>? {
        val record = preferences.getString(key, null) ?: return null

        return TextUtils.split(record, "‚‗‚").map { it.toInt() }.toTypedArray()
    }

}