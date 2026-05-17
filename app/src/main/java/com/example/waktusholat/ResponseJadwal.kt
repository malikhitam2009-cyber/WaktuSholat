package com.example.waktusholat.api

data class ResponseJadwal(
    val data: DataJadwal
)

data class DataJadwal(
    val id: Int,
    val lokasi: String,
    val daerah: String,
    val jadwal: Jadwal
)

data class Jadwal(
    val tanggal: String,
    val imsak: String,
    val subuh: String,
    val terbit: String,
    val dhuha: String,
    val dzuhur: String,
    val ashar: String,
    val maghrib: String,
    val isya: String
)