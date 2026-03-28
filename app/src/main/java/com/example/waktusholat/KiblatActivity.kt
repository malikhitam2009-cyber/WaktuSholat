package com.example.waktusholat

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class KiblatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val web = WebView(this)
        setContentView(web)

        web.settings.javaScriptEnabled = true
        web.loadUrl("https://qiblafinder.withgoogle.com/")
    }
}