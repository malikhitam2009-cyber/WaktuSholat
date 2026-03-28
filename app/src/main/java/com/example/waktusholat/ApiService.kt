package com.example.waktusholat

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("timings")
    fun getJadwal(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("method") method: Int = 2
    ): Call<ResponseJadwal>
}