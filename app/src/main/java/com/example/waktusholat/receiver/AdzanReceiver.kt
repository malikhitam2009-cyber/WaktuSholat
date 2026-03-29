package com.example.waktusholat.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.waktusholat.R

class AdzanReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val notif = NotificationCompat.Builder(context, "adzan_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Waktu Sholat")
            .setContentText("Sudah masuk waktu sholat")
            .build()

        // 🔥 CHECK PERMISSION DULU
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(1, notif)
        }

        try {
            val mp = MediaPlayer.create(context, R.raw.adzan)
            mp?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}