package com.example.waktusholat

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("sholat/kota/cari/{nama}")
    fun cariKota(
        @Path("nama") nama: String
    ): Call<ResponseCariKota>

    @GET("sholat/jadwal/{kota}/{tanggal}")
    fun getJadwal(
        @Path("kota") kota: String,
        @Path("tanggal") tanggal: String
    ): Call<ResponseJadwal>
}