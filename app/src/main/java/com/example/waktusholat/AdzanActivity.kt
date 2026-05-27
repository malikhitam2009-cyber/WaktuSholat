package com.example.waktusholat

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.waktusholat.receiver.AdzanReceiver
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AdzanActivity : AppCompatActivity() {

    private lateinit var txtLokasi: TextView
    private lateinit var txtSubuh: TextView
    private lateinit var txtDzuhur: TextView
    private lateinit var txtAshar: TextView
    private lateinit var txtMaghrib: TextView
    private lateinit var txtIsya: TextView

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_LOCATION_PERMISSION = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adzan)

        txtLokasi = findViewById(R.id.txtLokasi)
        txtSubuh = findViewById(R.id.txtSubuh)
        txtDzuhur = findViewById(R.id.txtDzuhur)
        txtAshar = findViewById(R.id.txtAshar)
        txtMaghrib = findViewById(R.id.txtMaghrib)
        txtIsya = findViewById(R.id.txtIsya)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val needsPermission = permissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (needsPermission.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, needsPermission.toTypedArray(), REQUEST_LOCATION_PERMISSION)
        } else {
            checkExactAlarmPermission()
        }
    }

    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Mohon izinkan 'Alarms & Reminders' agar adzan tepat waktu", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            } else {
                getUserLocation()
            }
        } else {
            getUserLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            checkExactAlarmPermission()
        }
    }

    private fun getUserLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                            if (addresses.isNotEmpty()) {
                                val cityName = addresses[0].subAdminArea ?: addresses[0].locality ?: "Jakarta"
                                runOnUiThread { fetchJadwalByCityName(cityName) }
                            }
                        }
                    } else {
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val cityName = addresses?.getOrNull(0)?.subAdminArea ?: "Jakarta"
                        fetchJadwalByCityName(cityName)
                    }
                } else {
                    fetchJadwalByCityName("Jakarta")
                }
            }
        } catch (e: SecurityException) {
            fetchJadwalByCityName("Jakarta")
        }
    }

    private fun fetchJadwalByCityName(cityName: String) {
        val cleanName = cityName.replace("Kota ", "").replace("Kabupaten ", "").trim()
        RetrofitClient.instance.cariKota(cleanName).enqueue(object : Callback<ResponseCariKota> {
            override fun onResponse(call: Call<ResponseCariKota>, response: Response<ResponseCariKota>) {
                val data = response.body()?.data
                if (!data.isNullOrEmpty()) {
                    val selected = data.find { it.lokasi.contains("KOTA", true) } ?: data[0]
                    txtLokasi.text = selected.lokasi
                    getJadwal(selected.id, SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
                }
            }
            override fun onFailure(call: Call<ResponseCariKota>, t: Throwable) {
                Toast.makeText(this@AdzanActivity, "Gagal Sinkron Jadwal", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getJadwal(idKota: String, tanggal: String) {
        RetrofitClient.instance.getJadwal(idKota, tanggal).enqueue(object : Callback<ResponseJadwal> {
            override fun onResponse(call: Call<ResponseJadwal>, response: Response<ResponseJadwal>) {
                val jadwal = response.body()?.data?.jadwal ?: return
                txtSubuh.text = jadwal.subuh
                txtDzuhur.text = jadwal.dzuhur
                txtAshar.text = jadwal.ashar
                txtMaghrib.text = jadwal.maghrib
                txtIsya.text = jadwal.isya

                setAlarm(jadwal.subuh, 1, "subuh")
                setAlarm(jadwal.dzuhur, 2, "dzuhur")
                setAlarm(jadwal.ashar, 3, "ashar")
                setAlarm(jadwal.maghrib, 4, "maghrib")
                setAlarm(jadwal.isya, 5, "isya")
            }
            override fun onFailure(call: Call<ResponseJadwal>, t: Throwable) {}
        })
    }

    private fun setAlarm(jam: String, requestCode: Int, jenis: String) {
        try {
            val parts = jam.take(5).split(":")
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, parts[0].toInt())
                set(Calendar.MINUTE, parts[1].toInt())
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (calendar.timeInMillis <= System.currentTimeMillis()) return

            val intent = Intent(this, AdzanReceiver::class.java).apply {
                putExtra("JENIS_SHOLAT", jenis)
                action = "ACTION_ADZAN_$requestCode"
            }

            val pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

            val alarmInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent)
            alarmManager.setAlarmClock(alarmInfo, pendingIntent)

            Log.d("ALARM_SET", "Alarm $jenis set untuk ${calendar.time}")
        } catch (e: Exception) { e.printStackTrace() }
    }
}