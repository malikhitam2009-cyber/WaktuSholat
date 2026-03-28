package com.example.waktusholat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class TasbihActivity : AppCompatActivity() {

    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasbih)

        val text = findViewById<TextView>(R.id.txtCount)
        val btnTap = findViewById<Button>(R.id.btnTasbih)
        val btnReset = findViewById<Button>(R.id.btnReset)

        btnTap.setOnClickListener {
            count++
            text.text = count.toString()
        }

        btnReset.setOnClickListener {
            count = 0
            text.text = "0"
        }
    }
}