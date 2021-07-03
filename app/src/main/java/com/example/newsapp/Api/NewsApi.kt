package com.example.newsapp.Api

import com.example.newsapp.Model.TopHeadlines
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    companion object {
        const val BASE_URL = "https://newsapi.org/v2/"
        const val API_KEY = "API_KEY"
    }

    @GET("top-headlines")
    suspend fun getHeadLines(
        @Query("pageSize") pageSize: Int = 100,
        @Query("category") category: String? = null,
        @Query("apiKey") api: String = API_KEY,
        @Query("country") country: String = "tr"
    ) : TopHeadlines

    @GET("top-headlines")
    suspend fun searchHeadlines(
        @Query("category") category: String?,
        @Query("country") country: String,
        @Query("page") page: Int,
        @Query("apiKey") api: String = API_KEY,
        @Query("q") query: String? = null
    ) : Response<TopHeadlines>

}