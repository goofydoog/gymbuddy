package com.example.gymbuddy

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { ctx ->
            val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // Pobieranie menedżera powiadomień

            val channelId = "workout_reminder_id" // ID kanału dla powiadomień
            val channelName = "Workout Reminder Notifications" // Nazwa kanału
            val importance = NotificationManager.IMPORTANCE_HIGH // Priorytet kanału

            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(notificationChannel)
            // Tworzenie i rejestracja kanału powiadomień

            val notificationBuilder = NotificationCompat.Builder(ctx, channelId)
                .setSmallIcon(R.drawable.weightlifting_icon) // Ikona powiadomienia
                .setContentTitle("Workout Reminder") // Tytuł powiadomienia
                .setContentText("Your workout is scheduled in 2 hours.") // Treść powiadomienia
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Priorytet powiadomienia

            notificationManager.notify(0, notificationBuilder.build()) // Wyświetlenie powiadomienia
        }
    }
}

