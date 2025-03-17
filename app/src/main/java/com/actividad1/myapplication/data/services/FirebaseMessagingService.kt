package com.actividad1.myapplication.data.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.actividad1.myapplication.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Registrar en el log los datos recibidos
        Log.d("FCM", "Mensaje recibido: ${remoteMessage.data}")

        // Si el mensaje incluye un payload de notificación
        remoteMessage.notification?.let {
            val title = it.title ?: "Notificación"
            val message = it.body ?: "Mensaje vacío"
            // Mostrar la notificación local utilizando la función auxiliar
            showNotification(applicationContext, title, message)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Nuevo token: $token")
        // Aquí puedes enviar el token a tu servidor si es necesario
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(context: Context, title: String, message: String) {
        val channelId = "my_channel_id"
        val notificationId = System.currentTimeMillis().toInt()

        // Crear el canal de notificaciones para Android O o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para notificaciones push"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        // Crear un Intent para abrir la MainActivity cuando se haga clic en la notificación
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Configurar el PendingIntent
        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        // Construir la notificación usando NotificationCompat.Builder
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent) // Asocia el PendingIntent a la notificación
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}