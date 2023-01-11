package com.example.mymvvmnewsapp.api

import com.example.mymvvmnewsapp.Session
import com.example.mymvvmnewsapp.models.NewsResponse
import com.example.mymvvmnewsapp.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String = Session.user.countryCode,
        @Query("category")
        category: String = "general",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchForNews(

        @Query("q")
        searchQuery: String ,
        @Query("page")
        pageNUmber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ) : Response<NewsResponse>
}