package com.atelier.clockwidget.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.atelier.clockwidget.work.ClockUpdateWorker

class TimezoneChangedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_TIME_CHANGED -> {
                ClockUpdateWorker.triggerImmediateUpdate(context)
            }
        }
    }
}
