package com.example.waktusholat

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TasbihActivity : AppCompatActivity() {

    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasbih)

        val txt = findViewById<TextView>(R.id.txtCount)
        val btn = findViewById<Button>(R.id.btnTasbih)

        btn.setOnClickListener {
            count++
            txt.text = count.toString()
        }
    }
}