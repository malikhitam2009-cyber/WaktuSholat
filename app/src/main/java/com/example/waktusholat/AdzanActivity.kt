package com.example.waktusholat

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

        // 🔥 INIT VIEW (ANTI NULL)
        txtSubuh = findViewById(R.id.txtSubuh)
        txtDzuhur = findViewById(R.id.txtDzuhur)
        txtAshar = findViewById(R.id.txtAshar)
        txtMaghrib = findViewById(R.id.txtMaghrib)
        txtIsya = findViewById(R.id.txtIsya)
        txtLokasi = findViewById(R.id.txtLokasi)

        // 🔥 AMBIL DATA DARI INTENT
        val kota = intent.getStringExtra("KOTA")

        if (kota.isNullOrEmpty()) {
            txtLokasi.text = "Kota tidak ditemukan"
            return
        }

        txtLokasi.text = "Memuat..."

        // 🔥 TANGGAL HARI INI
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

                    val body = response.body()
                    if (body == null) {
                        txtLokasi.text = "Data kosong"
                        return
                    }

                    val jadwal = body.data.jadwal

                    // 🔥 SET TEXT (AMAN)
                    txtSubuh.text = jadwal.subuh
                    txtDzuhur.text = jadwal.dzuhur
                    txtAshar.text = jadwal.ashar
                    txtMaghrib.text = jadwal.maghrib
                    txtIsya.text = jadwal.isya

                    txtLokasi.text = "Jadwal Hari Ini"
                }

                override fun onFailure(call: Call<ResponseJadwal>, t: Throwable) {
                    txtLokasi.text = "Tidak ada koneksi"
                    t.printStackTrace()
                }
            })
    }
}