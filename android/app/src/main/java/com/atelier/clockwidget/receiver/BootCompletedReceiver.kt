package com.atelier.clockwidget.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.atelier.clockwidget.work.ClockUpdateWorker

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            ClockUpdateWorker.enqueuePeriodicWork(context)
            ClockUpdateWorker.triggerImmediateUpdate(context)
        }
    }
}
