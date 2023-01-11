package com.example.mymvvmnewsapp.models

import com.example.mymvvmnewsapp.models.Article

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)