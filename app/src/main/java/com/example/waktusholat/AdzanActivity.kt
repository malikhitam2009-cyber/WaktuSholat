package com.example.waktusholat

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AdzanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adzan)

        val txt = findViewById<TextView>(R.id.txtJadwal)

        txt.text = """
            Subuh : 04:45
            Dzuhur : 12:05
            Ashar : 15:20
            Maghrib : 18:10
            Isya : 19:20
        """.trimIndent()
    }
}