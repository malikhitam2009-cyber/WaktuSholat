package com.example.waktusholat.api

data class ResponseJadwal(
    val data: Data
)

data class Data(
    val jadwal: Jadwal
)

data class Jadwal(
    val subuh: String,
    val dzuhur: String,
    val ashar: String,
    val maghrib: String,
    val isya: String
)