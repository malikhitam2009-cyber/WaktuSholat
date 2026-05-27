package com.example.waktusholat.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.waktusholat.ResponseJadwal
import com.example.waktusholat.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BOOT_RECEIVER", "HP Baru Nyala, Re-sync jadwal...")

            val sharedPref = context.getSharedPreferences("WAKTU_SHOLAT", Context.MODE_PRIVATE)
            val idKota = sharedPref.getString("id_kota", null)

            if (idKota != null) {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val tanggal = sdf.format(Date())

                RetrofitClient.instance.getJadwal(idKota, tanggal).enqueue(object : Callback<ResponseJadwal> {
                    override fun onResponse(call: Call<ResponseJadwal>, response: Response<ResponseJadwal>) {
                        if (response.isSuccessful) {
                            val jadwal = response.body()?.data?.jadwal ?: return
                            
                            setAlarm(context, jadwal.subuh, 1, "subuh")
                            setAlarm(context, jadwal.dzuhur, 2, "dzuhur")
                            setAlarm(context, jadwal.ashar, 3, "ashar")
                            setAlarm(context, jadwal.maghrib, 4, "maghrib")
                            setAlarm(context, jadwal.isya, 5, "isya")
                        }
                    }

                    override fun onFailure(call: Call<ResponseJadwal>, t: Throwable) {
                        Log.e("BOOT_RECEIVER", "Gagal ambil jadwal pas boot: ${t.message}")
                    }
                })
            }
        }
    }

    private fun setAlarm(context: Context, jam: String, requestCode: Int, jenis: String) {
        try {
            val parts = jam.take(5).split(":")
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, parts[0].toInt())
                set(Calendar.MINUTE, parts[1].toInt())
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // Jika jam sudah lewat hari ini, pasang buat besok
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            val intent = Intent(context, AdzanReceiver::class.java).apply {
                putExtra("JENIS_SHOLAT", jenis)
                action = "ACTION_ADZAN_$requestCode"
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context, 
                requestCode, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val alarmInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent)
            alarmManager.setAlarmClock(alarmInfo, pendingIntent)

            Log.d("BOOT_RECEIVER", "Alarm $jenis sukses dipasang ulang")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}