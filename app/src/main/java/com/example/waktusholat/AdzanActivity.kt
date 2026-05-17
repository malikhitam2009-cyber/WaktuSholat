package com.example.waktusholat

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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

    private lateinit var btnSetUtama: Button
    private lateinit var btnGantiKota: Button

    private var idKota: String = ""
    private var namaKota: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_adzan)

        txtSubuh = findViewById(R.id.txtSubuh)
        txtDzuhur = findViewById(R.id.txtDzuhur)
        txtAshar = findViewById(R.id.txtAshar)
        txtMaghrib = findViewById(R.id.txtMaghrib)
        txtIsya = findViewById(R.id.txtIsya)
        txtLokasi = findViewById(R.id.txtLokasi)

        btnSetUtama =
            findViewById(R.id.btnSetUtama)

        btnGantiKota =
            findViewById(R.id.btnGantiKota)

        // =========================
        // AMBIL DATA INTENT
        // =========================

        idKota =
            intent.getStringExtra("ID_KOTA") ?: ""

        namaKota =
            intent.getStringExtra("NAMA_KOTA") ?: ""

        val preview =
            intent.getBooleanExtra(
                "PREVIEW",
                false
            )

        // =========================
        // CEK DATA
        // =========================

        if (idKota.isEmpty()) {

            Toast.makeText(
                this,
                "Kota tidak ditemukan",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }

        txtLokasi.text = namaKota

        // =========================
        // TANGGAL
        // =========================

        val sdf = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        )

        val tanggal = sdf.format(Date())

        getJadwal(idKota, tanggal)

        // =========================
        // SAVE KOTA UTAMA
        // =========================

        btnSetUtama.setOnClickListener {

            val sharedPref =
                getSharedPreferences(
                    "WAKTU_SHOLAT",
                    MODE_PRIVATE
                )

            sharedPref.edit()
                .putString("ID_KOTA", idKota)
                .putString("NAMA_KOTA", namaKota)
                .apply()

            Toast.makeText(
                this,
                "Kota utama disimpan",
                Toast.LENGTH_SHORT
            ).show()
        }

        // =========================
        // GANTI KOTA
        // =========================

        btnGantiKota.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    PilihKotaActivity::class.java
                )
            )
        }

        // =========================
        // AUTO SAVE JIKA BUKAN PREVIEW
        // =========================

        if (!preview) {

            val sharedPref =
                getSharedPreferences(
                    "WAKTU_SHOLAT",
                    MODE_PRIVATE
                )

            sharedPref.edit()
                .putString("ID_KOTA", idKota)
                .putString("NAMA_KOTA", namaKota)
                .apply()
        }
    }

    // =========================
    // GET JADWAL
    // =========================

    private fun getJadwal(
        idKota: String,
        tanggal: String
    ) {

        RetrofitClient.instance
            .getJadwal(idKota, tanggal)

            .enqueue(object :
                Callback<ResponseJadwal> {

                override fun onResponse(
                    call: Call<ResponseJadwal>,
                    response: Response<ResponseJadwal>
                ) {

                    if (!response.isSuccessful) {

                        Toast.makeText(
                            this@AdzanActivity,
                            "Gagal mengambil jadwal",
                            Toast.LENGTH_SHORT
                        ).show()

                        return
                    }

                    val body = response.body()

                    if (body == null) {

                        Toast.makeText(
                            this@AdzanActivity,
                            "Data kosong",
                            Toast.LENGTH_SHORT
                        ).show()

                        return
                    }

                    val jadwal =
                        body.data.jadwal

                    txtSubuh.text =
                        jadwal.subuh

                    txtDzuhur.text =
                        jadwal.dzuhur

                    txtAshar.text =
                        jadwal.ashar

                    txtMaghrib.text =
                        jadwal.maghrib

                    txtIsya.text =
                        jadwal.isya

                    // =========================
                    // SET ALARM
                    // =========================

                    setAlarm(
                        jadwal.subuh,
                        1
                    )

                    setAlarm(
                        jadwal.dzuhur,
                        2
                    )

                    setAlarm(
                        jadwal.ashar,
                        3
                    )

                    setAlarm(
                        jadwal.maghrib,
                        4
                    )

                    setAlarm(
                        jadwal.isya,
                        5
                    )
                }

                override fun onFailure(
                    call: Call<ResponseJadwal>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@AdzanActivity,
                        "Tidak ada koneksi",
                        Toast.LENGTH_SHORT
                    ).show()

                    t.printStackTrace()
                }
            })
    }

    // =========================
    // SET ALARM
    // =========================

    private fun setAlarm(
        jam: String,
        requestCode: Int
    ) {

        try {

            val parts =
                jam.split(":")

            val hour =
                parts[0].toInt()

            val minute =
                parts[1].toInt()

            val calendar =
                Calendar.getInstance()

            calendar.set(
                Calendar.HOUR_OF_DAY,
                hour
            )

            calendar.set(
                Calendar.MINUTE,
                minute
            )

            calendar.set(
                Calendar.SECOND,
                0
            )

            if (
                calendar.timeInMillis <
                System.currentTimeMillis()
            ) {
                return
            }

            val intent = Intent(
                this,
                com.example.waktusholat.receiver.AdzanReceiver::class.java
            )

            val pendingIntent =
                PendingIntent.getBroadcast(
                    this,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or
                            PendingIntent.FLAG_IMMUTABLE
                )

            val alarmManager =
                getSystemService(ALARM_SERVICE)
                        as AlarmManager

            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.S
            ) {

                if (
                    alarmManager.canScheduleExactAlarms()
                ) {

                    alarmManager.setExact(
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

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }
}