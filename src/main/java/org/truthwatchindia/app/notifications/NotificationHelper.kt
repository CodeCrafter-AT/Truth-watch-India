package org.truthwatchindia.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.truthwatchindia.app.MainActivity
import org.truthwatchindia.app.R

object NotificationHelper {

    const val CHANNEL_ID = "breaking_news"
    const val CHANNEL_NAME = "ब्रेकिंग न्यूज़ (Breaking News)"
    const val CHANNEL_DESC = "Truth Watch India ताज़ा व महत्वपूर्ण समाचार अलर्ट"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNewsNotification(
        context: Context,
        postId: Long,
        title: String,
        excerpt: String
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("article_id", postId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            postId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Truth Watch India • ताज़ा ख़बर")
            .setContentText(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$title\n\n$excerpt"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(postId.toInt(), notification)
        } catch (_: SecurityException) {
            // Permission POST_NOTIFICATIONS may not be granted by user yet
        }
    }
}
