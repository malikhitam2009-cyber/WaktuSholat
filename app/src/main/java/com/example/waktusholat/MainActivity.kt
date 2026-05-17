package com.example.waktusholat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val menuAdzan =
            findViewById<CardView>(R.id.menuAdzan)

        val menuTasbih =
            findViewById<CardView>(R.id.menuTasbih)

        val menuKiblat =
            findViewById<CardView>(R.id.menuKiblat)

        // ADZAN
        menuAdzan.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    PilihKotaActivity::class.java
                )
            )
        }

        // TASBIH
        menuTasbih.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    TasbihActivity::class.java
                )
            )
        }

        // KIBLAT
        menuKiblat.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    KiblatActivity::class.java
                )
            )
        }
    }
}