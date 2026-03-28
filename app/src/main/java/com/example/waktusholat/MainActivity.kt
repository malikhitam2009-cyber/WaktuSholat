package com.example.waktusholat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnAdzan = findViewById<Button>(R.id.btnAdzan)
        val btnTasbih = findViewById<Button>(R.id.btnTasbih)
        val btnKiblat = findViewById<Button>(R.id.btnKiblat)

        btnAdzan.setOnClickListener {
            startActivity(Intent(this, AdzanActivity::class.java))
        }

        btnTasbih.setOnClickListener {
            startActivity(Intent(this, TasbihActivity::class.java))
        }

        btnKiblat.setOnClickListener {
            startActivity(Intent(this, KiblatActivity::class.java))
        }
    }
}