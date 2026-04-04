package com.example.waktusholat.api

data class ResponseKota(
    val data: List<Kota>
)

data class Kota(
    val id: String,
    val lokasi: String
)