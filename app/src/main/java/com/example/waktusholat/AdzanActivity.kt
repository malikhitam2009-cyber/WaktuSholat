package com.example.waktusholat

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
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

        // Init Views
        txtLokasi = findViewById(R.id.txtLokasi)
        txtSubuh = findViewById(R.id.txtSubuh)
        txtDzuhur = findViewById(R.id.txtDzuhur)
        txtAshar = findViewById(R.id.txtAshar)
        txtMaghrib = findViewById(R.id.txtMaghrib)
        txtIsya = findViewById(R.id.txtIsya)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            getUserLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation()
            } else {
                Toast.makeText(this, "Izin lokasi ditolak, menggunakan Jakarta", Toast.LENGTH_SHORT).show()
                fetchJadwalByCityName("Jakarta")
            }
        }
    }

    private fun getUserLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val cityName = addresses[0].subAdminArea ?: addresses[0].locality ?: "Jakarta"
                        // subAdminArea biasanya nama Kabupaten/Kota di Indonesia
                        fetchJadwalByCityName(cityName)
                    } else {
                        fetchJadwalByCityName("Jakarta")
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
                val kotaList = response.body()?.data
                if (!kotaList.isNullOrEmpty()) {
                    val cityId = kotaList[0].id
                    val locationLabel = kotaList[0].lokasi
                    txtLokasi.text = locationLabel
                    
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    getJadwal(cityId, sdf.format(Date()))
                } else {
                    // Fallback jika nama kota tidak ditemukan di API
                    if (cleanName != "Jakarta") fetchJadwalByCityName("Jakarta")
                }
            }

            override fun onFailure(call: Call<ResponseCariKota>, t: Throwable) {
                Toast.makeText(this@AdzanActivity, "Gagal mencari kota", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getJadwal(idKota: String, tanggal: String) {
        RetrofitClient.instance.getJadwal(idKota, tanggal).enqueue(object : Callback<ResponseJadwal> {
            override fun onResponse(call: Call<ResponseJadwal>, response: Response<ResponseJadwal>) {
                if (response.isSuccessful) {
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
            }

            override fun onFailure(call: Call<ResponseJadwal>, t: Throwable) {
                Toast.makeText(this@AdzanActivity, "Koneksi bermasalah", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setAlarm(jam: String, requestCode: Int, jenis: String) {
        try {
            val cleanJam = jam.take(5).trim()
            val parts = cleanJam.split(":")
            if (parts.size < 2) return

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, parts[0].toInt())
            calendar.set(Calendar.MINUTE, parts[1].toInt())
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            if (calendar.timeInMillis <= System.currentTimeMillis()) return

            val intent = Intent(this, AdzanReceiver::class.java).apply {
                putExtra("JENIS_SHOLAT", jenis)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                this, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
