package com.example.waktusholat.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class AdzanReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val jenis =
            intent.getStringExtra(
                "JENIS_SHOLAT"
            )

        val serviceIntent =
            Intent(
                context,
                AdzanService::class.java
            )

        serviceIntent.putExtra(
            "JENIS_SHOLAT",
            jenis
        )

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            context.startForegroundService(
                serviceIntent
            )

        } else {

            context.startService(
                serviceIntent
            )
        }
    }
}