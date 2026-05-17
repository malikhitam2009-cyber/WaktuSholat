package com.example.waktusholat

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.waktusholat.api.Kota
import com.example.waktusholat.api.ResponseKota
import com.example.waktusholat.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PilihKotaActivity : AppCompatActivity() {

    private lateinit var containerKota: LinearLayout
    private lateinit var edtSearch: EditText

    // SIMPAN SEMUA KOTA
    private var semuaKota = listOf<Kota>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pilih_kota)

        // INIT VIEW
        containerKota = findViewById(R.id.containerKota)
        edtSearch = findViewById(R.id.edtSearch)

        // AMBIL DATA KOTA
        getSemuaKota()

        // SEARCH REALTIME
        edtSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

                filterKota(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    // ====================================
    // GET SEMUA KOTA
    // ====================================

    private fun getSemuaKota() {

        RetrofitClient.instance.getKota()
            .enqueue(object : Callback<ResponseKota> {

                override fun onResponse(
                    call: Call<ResponseKota>,
                    response: Response<ResponseKota>
                ) {

                    if (!response.isSuccessful) {

                        Toast.makeText(
                            this@PilihKotaActivity,
                            "Gagal ambil data kota",
                            Toast.LENGTH_SHORT
                        ).show()

                        return
                    }

                    val body = response.body()

                    if (body == null) {

                        Toast.makeText(
                            this@PilihKotaActivity,
                            "Data kosong",
                            Toast.LENGTH_SHORT
                        ).show()

                        return
                    }

                    // SIMPAN SEMUA KOTA
                    semuaKota = body.data

                    // TAMPILKAN
                    tampilkanKota(semuaKota)
                }

                override fun onFailure(
                    call: Call<ResponseKota>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@PilihKotaActivity,
                        "Tidak ada koneksi",
                        Toast.LENGTH_SHORT
                    ).show()

                    t.printStackTrace()
                }
            })
    }

    // ====================================
    // FILTER SEARCH
    // ====================================

    private fun filterKota(keyword: String) {

        // KALAU SEARCH KOSONG
        if (keyword.isEmpty()) {

            tampilkanKota(semuaKota)

            return
        }

        // FILTER
        val hasilFilter = semuaKota.filter {

            it.lokasi.lowercase()
                .contains(keyword.lowercase())
        }

        tampilkanKota(hasilFilter)
    }

    // ====================================
    // TAMPILKAN KOTA
    // ====================================

    private fun tampilkanKota(listKota: List<Kota>) {

        // HAPUS SEMUA BUTTON
        containerKota.removeAllViews()

        // LIMIT Biar ringan
        val limitedList = listKota.take(50)

        for (kota in limitedList) {

            val button = Button(this)

            button.text = kota.lokasi

            button.textSize = 16f

            button.setPadding(
                20,
                30,
                20,
                30
            )

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.bottomMargin = 16

            button.layoutParams = params

            // CLICK BUTTON
            button.setOnClickListener {

                val intent = Intent(
                    this,
                    AdzanActivity::class.java
                )

                intent.putExtra("ID_KOTA", kota.id)

                intent.putExtra("NAMA_KOTA", kota.lokasi)

                startActivity(intent)

                finish()
            }

            containerKota.addView(button)
        }
    }
}