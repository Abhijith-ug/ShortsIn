package com.example.mymvvmnewsapp.repository

import android.content.Context
import com.example.mymvvmnewsapp.models.Article
import com.example.mymvvmnewsapp.util.Resource
import com.example.mymvvmnewsapp.util.UiState

interface NewsRepository {
    suspend fun getSavedArticles(): Resource<List<Article>>
    suspend fun saveArticle(article: Article): Resource<Boolean>
    suspend fun deleteArticleFromFireStore(article: Article, result: (UiState<String>) -> Unit)
    suspend fun isArticleExistInFireStore(url: String, result: (UiState<Boolean>) -> Unit)
}
