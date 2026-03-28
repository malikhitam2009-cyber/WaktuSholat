package com.example.waktusholat

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_main)

            val btnAdzan = findViewById<View>(R.id.btnAdzan)
            val btnTasbih = findViewById<View>(R.id.btnTasbih)
            val btnKiblat = findViewById<View>(R.id.btnKiblat)

            btnAdzan.setOnClickListener {
                startActivity(Intent(this, AdzanActivity::class.java))
            }

            btnTasbih.setOnClickListener {
                startActivity(Intent(this, TasbihActivity::class.java))
            }

            btnKiblat.setOnClickListener {
                startActivity(Intent(this, KiblatActivity::class.java))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}