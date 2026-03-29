package com.example.waktusholat

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class PilihKotaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilih_kota)
    }

    fun pilihKota(view: View) {
        val kode = view.tag.toString()

        val intent = Intent(this, AdzanActivity::class.java)
        intent.putExtra("KOTA", kode)
        startActivity(intent)
    }
}