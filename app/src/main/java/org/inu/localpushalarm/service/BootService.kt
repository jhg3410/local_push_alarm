package org.inu.localpushalarm.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.inu.localpushalarm.util.WakeLockUtil


class BootService: Service() {

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
    private val wakeLockUtil = WakeLockUtil()

    override fun onCreate() {
        super.onCreate()

        wakeLockUtil.acquireCpuWakeLock(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        wakeLockUtil.releaseCpuWakeLock()
    }
}