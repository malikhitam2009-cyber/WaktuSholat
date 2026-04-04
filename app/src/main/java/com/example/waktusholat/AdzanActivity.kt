package com.example.waktusholat

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.waktusholat.api.RetrofitClient
import com.example.waktusholat.api.ResponseJadwal
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class AdzanActivity : AppCompatActivity() {

    private lateinit var txtSubuh: TextView
    private lateinit var txtDzuhur: TextView
    private lateinit var txtAshar: TextView
    private lateinit var txtMaghrib: TextView
    private lateinit var txtIsya: TextView
    private lateinit var txtLokasi: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adzan)

        txtSubuh = findViewById(R.id.txtSubuh)
        txtDzuhur = findViewById(R.id.txtDzuhur)
        txtAshar = findViewById(R.id.txtAshar)
        txtMaghrib = findViewById(R.id.txtMaghrib)
        txtIsya = findViewById(R.id.txtIsya)
        txtLokasi = findViewById(R.id.txtLokasi)

        val kota = intent.getStringExtra("KOTA")

        if (kota.isNullOrEmpty()) {
            txtLokasi.text = "Kota tidak ditemukan"
            return
        }

        txtLokasi.text = "Memuat..."

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val tanggal = sdf.format(Date())

        getJadwal(kota, tanggal)
    }

    private fun getJadwal(kota: String, tanggal: String) {

        RetrofitClient.instance.getJadwal(kota, tanggal)
            .enqueue(object : Callback<ResponseJadwal> {

                override fun onResponse(
                    call: Call<ResponseJadwal>,
                    response: Response<ResponseJadwal>
                ) {

                    if (!response.isSuccessful) {
                        txtLokasi.text = "Gagal ambil data"
                        return
                    }

                    val body = response.body() ?: return
                    val jadwal = body.data.jadwal

                    txtSubuh.text = jadwal.subuh
                    txtDzuhur.text = jadwal.dzuhur
                    txtAshar.text = jadwal.ashar
                    txtMaghrib.text = jadwal.maghrib
                    txtIsya.text = jadwal.isya

                    txtLokasi.text = "Jadwal Hari Ini"

                    // 🔥 SET ALARM OTOMATIS
                    setAlarm(jadwal.subuh, 1)
                    setAlarm(jadwal.dzuhur, 2)
                    setAlarm(jadwal.ashar, 3)
                    setAlarm(jadwal.maghrib, 4)
                    setAlarm(jadwal.isya, 5)
                }

                override fun onFailure(call: Call<ResponseJadwal>, t: Throwable) {
                    txtLokasi.text = "Tidak ada koneksi"
                    t.printStackTrace()
                }
            })
    }

    // 🔥 FUNCTION ALARM
    private fun setAlarm(jam: String, requestCode: Int) {

        val parts = jam.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        if (calendar.timeInMillis < System.currentTimeMillis()) return

        val intent = Intent(this, com.example.waktusholat.receiver.AdzanReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {

                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }

            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }

        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}