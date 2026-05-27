package com.example.waktusholat.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.waktusholat.R

class AdzanService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        createNotification()

        try {
            mediaPlayer?.release()

            val jenis = intent?.getStringExtra("JENIS_SHOLAT")?.lowercase()

            val audioRes = when (jenis) {
                "subuh" -> R.raw.adzan2
                "maghrib" -> R.raw.adzan_mekkah
                "dzuhur", "ashar", "isya" -> R.raw.adzan
                else -> R.raw.adzan
            }

            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            mediaPlayer = MediaPlayer.create(this, audioRes, attributes, 0)

            mediaPlayer?.apply {
                setVolume(1.0f, 1.0f)
                isLooping = false
                start()
                setOnCompletionListener {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return START_NOT_STICKY
    }

    private fun createNotification() {
        val channelId = "adzan_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Adzan",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Waktu Sholat")
            .setContentText("Adzan sedang diputar")
            .setSmallIcon(R.drawable.ic_logo)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}