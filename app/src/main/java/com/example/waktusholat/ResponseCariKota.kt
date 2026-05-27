package com.example.waktusholat

data class ResponseCariKota(
    val status: Boolean,
    val data: List<Kota>?
)

data class Kota(
    val id: String,
    val lokasi: String
)