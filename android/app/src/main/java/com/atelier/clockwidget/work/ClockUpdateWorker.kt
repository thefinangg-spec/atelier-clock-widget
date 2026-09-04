package com.atelier.clockwidget.work

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.*
import com.atelier.clockwidget.glance.ClockGlanceWidget
import java.util.concurrent.TimeUnit

class ClockUpdateWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            ClockGlanceWidget().updateAll(appContext)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "atelier_clock_periodic_update"

        fun enqueuePeriodicWork(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .build()

            val updateRequest = PeriodicWorkRequestBuilder<ClockUpdateWorker>(
                15, TimeUnit.MINUTES,
                5, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                updateRequest
            )
        }

        fun triggerImmediateUpdate(context: Context) {
            val oneTimeRequest = OneTimeWorkRequestBuilder<ClockUpdateWorker>()
                .build()
            WorkManager.getInstance(context).enqueue(oneTimeRequest)
        }
    }
}
