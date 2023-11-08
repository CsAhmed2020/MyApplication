package com.ayamedica.myapplication

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import kotlin.random.Random


/*
open class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var notificationId = 1

    @Inject
    lateinit var userPreferences: UserPreferences

    @SuppressLint("UnspecifiedImmutableFlag", "NewApi")
    private fun sendNotification(title: String?, body: String?, extras: Bundle?) {

        val intent = Intent(
            this,
            MainActivity::class.java
        )

        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        extras?.keySet()?.forEach {
            intent.putExtra(it, extras.getString(it))
        }

        val pendingIntent: PendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(
                    this,
                    Random.nextInt(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            } else {
                PendingIntent.getActivity(
                    this,
                    Random.nextInt(),
                    intent,
                    PendingIntent.FLAG_ONE_SHOT
                )
            }

        val notificationBuilder =
            NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .apply {
                    setContentTitle(title)
                    setContentText(body)
                    setStyle(NotificationCompat.BigTextStyle().bigText(body))
                    setAutoCancel(true)
                    setSound(
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    )
                    setContentIntent(pendingIntent)
                    setLargeIcon(
                        BitmapFactory.decodeResource(
                            resources,
                            R.drawable.swipy_icon_dark_medium
                        )
                    )
                    setDefaults(Notification.DEFAULT_VIBRATE)
                    setSmallIcon(R.drawable.swipy_icon_dark_small)
                    setDefaults(Notification.DEFAULT_ALL)
                }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes: AudioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val channel = NotificationChannel(
                getString(R.string.default_notification_channel_id),
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.setShowBadge(true)
            channel.canShowBadge()
            channel.setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                audioAttributes
            )
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
            notificationManager.createNotificationChannel(channel)

        }
        notificationManager.notify(++notificationId, notificationBuilder.build())

        getBadgeNumber()

    }

    private fun getBadgeNumber() {
        CoroutineScope(Dispatchers.IO).launch {
            */
/* homeRepositoryImpl.countUnreadMessages().collect {
                 when (it) {
                     is NetworkState.Result<*> -> {
                         val count =
                             (it.response as CountMyUnReadMessagesQuery.Data).readCountMyUnReadMessages
                         updateBadgeNumber(applicationContext, count)
                     }
                     else -> Unit
                 }
             }*//*

        }
    }

    @SuppressLint("NewApi")
    override fun handleIntent(intent: Intent) {


        val title = intent.extras?.getString("gcm.notification.title")
        val body = intent.extras?.getString("gcm.notification.body")

//        intent.extras?.keySet()?.forEach {
//            Log.e(TAG, "onNewIntent: $it ${intent.extras?.get(it)} ${intent.extras?.get(it)?.javaClass}")
//        }

        if (!(title.isNullOrEmpty() && body.isNullOrEmpty()))
            pushNotifications(title, body, intent.extras)

    }

    private fun pushNotifications(title: String?, body: String?, extras: Bundle?) {
        sendNotification(title, body, extras)
    }

    companion object {
        private const val CHANNEL_NAME = "FCM"
        private const val TAG = "MY-FCM"

        fun updateBadgeNumber(context: Context, badgeCount: Int) {

//            ShortcutBadger.applyCount(context, badgeCount)
        }

    }

}*/
