package com.dicoding.asclepius.data.remote.retrofit

import com.dicoding.asclepius.data.remote.response.HealthNewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("top-headlines")
    fun getNews(
        @Query("q") query: String = "cancer",
        @Query("category") category: String = "health",
        @Query("language") language: String = "en",
    ): Call<HealthNewsResponse>
}