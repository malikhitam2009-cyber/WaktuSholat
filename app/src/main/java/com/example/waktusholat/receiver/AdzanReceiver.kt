package com.example.waktusholat.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log

class AdzanReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val jenis = intent.getStringExtra("JENIS_SHOLAT")
        Log.d("ADZAN_RECEIVER", "Alarm masuk untuk: $jenis")

        // Paksa CPU bangun biar kodingan di bawah sempet jalan
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "WaktuSholat:AdzanWakeLock"
        )
        wakeLock.acquire(30000L /* 30 detik saja */)

        val serviceIntent = Intent(context, AdzanService::class.java)
        serviceIntent.putExtra("JENIS_SHOLAT", jenis)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}