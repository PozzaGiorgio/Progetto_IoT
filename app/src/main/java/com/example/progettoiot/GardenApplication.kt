package com.example.progettoiot

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.*
import com.example.progettoiot.worker.MoistureMonitorWorker
import java.util.concurrent.TimeUnit

class GardenApplication : Application() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "garden_notifications"
        const val MOISTURE_WORK_NAME = "moisture_monitoring"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        schedulePeriodicMoistureCheck()
    }

    private fun createNotificationChannel() {
        // I canali di notifica sono disponibili solo da API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Notifiche Giardino",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifiche per l'irrigazione del giardino"
                enableVibration(true)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun schedulePeriodicMoistureCheck() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val moistureWorkRequest = PeriodicWorkRequestBuilder<MoistureMonitorWorker>(
            15, TimeUnit.MINUTES // Controlla ogni 15 minuti
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            MOISTURE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            moistureWorkRequest
        )
    }
}