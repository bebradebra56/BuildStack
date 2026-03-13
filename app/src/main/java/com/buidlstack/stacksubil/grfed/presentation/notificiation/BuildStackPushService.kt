package com.buidlstack.stacksubil.grfed.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.buidlstack.stacksubil.BuildStackActivity
import com.buidlstack.stacksubil.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val BUILD_STACK_CHANNEL_ID = "build_stack_notifications"
private const val BUILD_STACK_CHANNEL_NAME = "BuildStack Notifications"
private const val BUILD_STACK_NOT_TAG = "BuildStack"

class BuildStackPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                buildStackShowNotification(it.title ?: BUILD_STACK_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                buildStackShowNotification(it.title ?: BUILD_STACK_NOT_TAG, it.body ?: "", data = null)
            }
        }

    }

    private fun buildStackShowNotification(title: String, message: String, data: String?) {
        val buildStackNotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                BUILD_STACK_CHANNEL_ID,
                BUILD_STACK_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            buildStackNotificationManager.createNotificationChannel(channel)
        }

        val buildStackIntent = Intent(this, BuildStackActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val buildStackPendingIntent = PendingIntent.getActivity(
            this,
            0,
            buildStackIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val buildStackNotification = NotificationCompat.Builder(this, BUILD_STACK_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.build_strack_noti_ic)
            .setAutoCancel(true)
            .setContentIntent(buildStackPendingIntent)
            .build()

        buildStackNotificationManager.notify(System.currentTimeMillis().toInt(), buildStackNotification)
    }

}