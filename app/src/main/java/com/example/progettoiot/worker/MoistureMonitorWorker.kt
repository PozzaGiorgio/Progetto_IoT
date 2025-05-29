package com.example.progettoiot.worker

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker
import com.example.progettoiot.GardenApplication
import com.example.progettoiot.repository.GardenRepository
import com.example.progettoiot.repository.Result

class MoistureMonitorWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val repository = GardenRepository()
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): ListenableWorker.Result {
        return try {
            val result = repository.getMoistureStatus()

            return when {
                result is com.example.progettoiot.repository.Result.Success -> {
                    if (result.data.stato == "secco") {
                        sendDryNotification()
                    }
                    ListenableWorker.Result.success()
                }
                result is com.example.progettoiot.repository.Result.Error -> {
                    ListenableWorker.Result.success()
                }
                result is com.example.progettoiot.repository.Result.Loading -> {
                    ListenableWorker.Result.success()
                }
                else -> ListenableWorker.Result.failure()
            }
        } catch (e: Exception) {
            ListenableWorker.Result.failure()
        }
    }

    private fun sendDryNotification() {
        val notificationBuilder = NotificationCompat.Builder(applicationContext, GardenApplication.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(com.example.progettoiot.R.drawable.ic_notification)
            .setContentTitle("🌱 Terreno Secco!")
            .setContentText("Le tue piante hanno bisogno di acqua. Tocca per aprire l'app.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Vibrazione compatibile con API precedenti
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Per Android 8.0+, la vibrazione è gestita dal canale
        } else {
            // Per Android precedenti, aggiungi la vibrazione alla notifica
            notificationBuilder.setVibrate(longArrayOf(0, 1000, 500, 1000))
        }

        val notification = notificationBuilder.build()
        notificationManager.notify(1, notification)
    }
}