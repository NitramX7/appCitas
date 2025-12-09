package com.example.appcitas

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import pantallas.MisCitas

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_ID = "citas_channel"
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.data["title"] ?: "Nueva notificación"
        val body = message.data["body"] ?: "Tienes una nueva cita"

        crearCanalNotificaciones()
        mostrarNotificacion(title, body)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        android.util.Log.d("FCM_TOKEN", "Nuevo token: $token")
    }

    private fun mostrarNotificacion(title: String, body: String) {
        val intent = Intent(this, MisCitas::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        NotificationManagerCompat.from(this)
            .notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificaciones de citas"
            val descriptionText = "Avisos relacionados con tus citas"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
