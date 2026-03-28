package com.example.waktusholat

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class AdzanActivity : AppCompatActivity() {

    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var api: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adzan)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.aladhan.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ApiService::class.java)

        getLocation()
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude

                ambilJadwal(lat, lon)
                tampilkanKota(lat, lon)
            } else {
                Toast.makeText(this, "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun tampilkanKota(lat: Double, lon: Double) {
        val geo = Geocoder(this, Locale.getDefault())
        val alamat = geo.getFromLocation(lat, lon, 1)

        if (!alamat.isNullOrEmpty()) {
            findViewById<TextView>(R.id.txtKota).text = alamat[0].locality
        }
    }

    private fun ambilJadwal(lat: Double, lon: Double) {
        api.getJadwal(lat, lon).enqueue(object : Callback<ResponseJadwal> {

            override fun onResponse(call: Call<ResponseJadwal>, response: Response<ResponseJadwal>) {
                val t = response.body()?.data?.timings

                findViewById<TextView>(R.id.txtSubuh).text = t?.Fajr
                findViewById<TextView>(R.id.txtDzuhur).text = t?.Dhuhr
                findViewById<TextView>(R.id.txtAshar).text = t?.Asr
                findViewById<TextView>(R.id.txtMaghrib).text = t?.Maghrib
                findViewById<TextView>(R.id.txtIsya).text = t?.Isha
            }

            override fun onFailure(call: Call<ResponseJadwal>, t: Throwable) {
                Toast.makeText(this@AdzanActivity, "Gagal ambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}