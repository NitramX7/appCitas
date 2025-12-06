package com.example.appcitas

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class AppCitas : Application() {

    companion object {
        const val CHANNEL_ID_SOLICITUDES = "solicitudes_channel"
    }

    override fun onCreate() {
        super.onCreate()
        crearCanalSolicitudes()
    }

    private fun crearCanalSolicitudes() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "Solicitudes de citas"
            val descripcion = "Notificaciones cuando alguien comparte citas contigo"
            val importancia = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(
                CHANNEL_ID_SOLICITUDES,
                nombre,
                importancia
            ).apply {
                description = descripcion
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
