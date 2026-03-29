package com.example.waktusholat

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class KiblatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val web = WebView(this)
        setContentView(web)

        // INI YANG PENTING
        web.webViewClient = WebViewClient()
        class KiblatActivity : AppCompatActivity() {

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_kiblat)

                val web = findViewById<WebView>(R.id.webKiblat)
                web.settings.javaScriptEnabled = true
                web.loadUrl("https://qiblafinder.withgoogle.com/")
            }
        }
        web.settings.javaScriptEnabled = true

        web.loadUrl("https://qiblafinder.withgoogle.com/")
    }
}