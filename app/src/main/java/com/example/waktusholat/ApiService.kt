package com.example.waktusholat.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("sholat/jadwal/{kota}/{tanggal}")
    fun getJadwal(
        @Path("kota") kota: String,
        @Path("tanggal") tanggal: String
    ): Call<ResponseJadwal>

    @GET("sholat/kota/semua")
    fun getKota(): Call<ResponseKota>
}