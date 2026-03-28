package com.example.waktusholat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class TasbihActivity : AppCompatActivity() {

    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasbih)

        val text = findViewById<TextView>(R.id.txtCount)
        val button = findViewById<Button>(R.id.btnTasbih)

        button.setOnClickListener {
            count++
            text.text = count.toString()
        }
    }
}