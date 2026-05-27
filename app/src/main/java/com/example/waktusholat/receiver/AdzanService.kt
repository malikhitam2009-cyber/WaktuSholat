package com.example.waktusholat.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.waktusholat.R

class AdzanService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val jenis = intent?.getStringExtra("JENIS_SHOLAT")?.lowercase() ?: "sholat"
        Log.d("ADZAN_SERVICE", "Service Started for: $jenis")
        
        // Buat notif duluan supaya gak di-kill sistem
        createNotification()

        try {
            mediaPlayer?.release()

            val audioRes = when (jenis) {
                "subuh" -> R.raw.adzan2
                "maghrib" -> R.raw.adzan_mekkah
                else -> R.raw.adzan
            }

            // Gunakan USAGE_ALARM biar ngikut volume ALARM HP (ikon jam)
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            mediaPlayer = MediaPlayer.create(this, audioRes, attributes, 0)
            
            if (mediaPlayer == null) {
                Log.e("ADZAN_SERVICE", "Gagal create MediaPlayer!")
                stopSelf()
                return START_NOT_STICKY
            }

            mediaPlayer?.apply {
                // Jaga CPU tetep hidup sampe adzan beres
                setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
                setVolume(1.0f, 1.0f)
                start()
                
                setOnCompletionListener {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }

        } catch (e: Exception) {
            Log.e("ADZAN_SERVICE", "Error: ${e.message}")
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun createNotification() {
        val channelId = "adzan_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Jadwal Sholat",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(null, null)
                enableVibration(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Waktu Sholat")
            .setContentText("Adzan sedang berkumandang...")
            .setSmallIcon(R.drawable.ic_logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(1, notification)
        }
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}