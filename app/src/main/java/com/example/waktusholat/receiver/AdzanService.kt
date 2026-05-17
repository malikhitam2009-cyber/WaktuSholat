package com.example.waktusholat.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioManager
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

        // =========================
        // LIST AUDIO ADZAN
        // =========================

        val adzanList = listOf(

            R.raw.adzan,
            R.raw.adzan2,
            R.raw.adzan_mekkah
        )

        // RANDOM AUDIO
        val randomAdzan = adzanList.random()

        try {

            // =========================
            // MEDIA PLAYER
            // =========================

            mediaPlayer = MediaPlayer()

            // STREAM ALARM
            mediaPlayer?.setAudioStreamType(
                AudioManager.STREAM_ALARM
            )

            // AMBIL FILE AUDIO
            val afd =
                resources.openRawResourceFd(
                    randomAdzan
                )

            mediaPlayer?.setDataSource(
                afd.fileDescriptor,
                afd.startOffset,
                afd.length
            )

            afd.close()

            // LOOP FALSE
            mediaPlayer?.isLooping = false

            // PREPARE
            mediaPlayer?.prepare()

            // START AUDIO
            mediaPlayer?.start()

            // AUTO STOP
            mediaPlayer?.setOnCompletionListener {

                stopSelf()
            }

        } catch (e: Exception) {

            e.printStackTrace()

            stopSelf()
        }

        return START_STICKY
    }

    // =========================
    // NOTIFICATION
    // =========================

    private fun createNotification() {

        val channelId = "adzan_channel"

        // ANDROID 8+
        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel = NotificationChannel(
                channelId,
                "Adzan Notification",
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.description =
                "Channel untuk adzan"

            val manager =
                getSystemService(
                    NotificationManager::class.java
                )

            manager.createNotificationChannel(
                channel
            )
        }

        val notification: Notification =

            NotificationCompat.Builder(
                this,
                channelId
            )

                .setContentTitle(
                    "Waktu Sholat"
                )

                .setContentText(
                    "Adzan sedang berkumandang"
                )

                .setSmallIcon(
                    R.drawable.ic_logo
                )

                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )

                .setOngoing(true)

                .build()

        startForeground(
            1,
            notification
        )
    }

    // =========================
    // DESTROY
    // =========================

    override fun onDestroy() {

        try {

            mediaPlayer?.stop()

        } catch (_: Exception) {
        }

        mediaPlayer?.release()

        mediaPlayer = null

        super.onDestroy()
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? {

        return null
    }
}