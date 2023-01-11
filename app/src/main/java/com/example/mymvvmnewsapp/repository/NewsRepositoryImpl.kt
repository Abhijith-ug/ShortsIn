package com.example.mymvvmnewsapp.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.mymvvmnewsapp.Session
import com.example.mymvvmnewsapp.api.RetrofitInstance
import com.example.mymvvmnewsapp.database.ArticleDatabase
import com.example.mymvvmnewsapp.models.Article
import com.example.mymvvmnewsapp.models.ExclusiveNews
import com.example.mymvvmnewsapp.util.Constants
import com.example.mymvvmnewsapp.util.Resource
import com.example.mymvvmnewsapp.util.UiState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NewsRepositoryImpl(
    private val db: ArticleDatabase
) {
    private val articleCollectionRef = Firebase.firestore.collection("articles")
    private val exclusiveCollectionRef = Firebase.firestore.collection("exclusive")

    suspend fun getBreakingNews(countryCode: String, category: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode,category, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) = RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    //fun getSavedArticle(url: String) = db.getArticleDao().getArticle(url)

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

    suspend fun isArticleExist(article: Article) = db.getArticleDao().isArticleExist(
        article.url!!
    )

    // Store to firebase.
    suspend fun getSavedArticles(): Resource<List<Article>> = suspendCoroutine { continuation ->
        articleCollectionRef
            .whereEqualTo(Constants.UID, com.example.mymvvmnewsapp.Session.user.uid)
            .get().addOnSuccessListener {
                try {
                    continuation.resume(Resource.Success(it.toObjects()))
                } catch (e: Exception) {
                    continuation.resume(Resource.Error(e.message!!, null))
                }
            }.addOnFailureListener {
                continuation.resume(Resource.Error(it.message!!, null))
            }
    }

    suspend fun saveArticle(article: Article): Resource<Boolean> = suspendCoroutine { continuation ->
        articleCollectionRef.add(article).addOnSuccessListener {
            continuation.resume(Resource.Success(true))
        }.addOnFailureListener {
            continuation.resume(Resource.Error(it.message!!, null))
        }
    }

    suspend fun deleteArticleFromFireStore(article: Article, result: (UiState<String>) -> Unit) {
        val articleQuery = articleCollectionRef.whereEqualTo("title", article.title)
            .whereEqualTo("url", article.url)
            .whereEqualTo(Constants.UID, com.example.mymvvmnewsapp.Session.user.uid)
            .get().await()
        if (articleQuery.documents.isNotEmpty()) {
            for (document in articleQuery) {
                try {
                    articleCollectionRef.document(document.id).delete().addOnSuccessListener {
                        result.invoke(
                            UiState.Success("Article deleted")
                        )
                    }.addOnFailureListener {
                        result.invoke(
                            UiState.Failure(
                                it.localizedMessage
                            )
                        )
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        //Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                //Toast.makeText(activity, "No article matched the query", Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun isArticleExistInFireStore(
        url: String, result: (UiState<Boolean>) -> Unit
    ) {
        try {
            val articleQuery = articleCollectionRef.whereEqualTo("url", url)
                .whereEqualTo("uid", com.example.mymvvmnewsapp.Session.user.uid)
                .get().await()
            if (articleQuery.documents.isNotEmpty()) {
                result.invoke(UiState.Success(true))
            } else {
                result.invoke(UiState.Success(false))
            }
        } catch (e: Exception) {
            result.invoke(UiState.Failure("Something went wrong!"))
        }
    }

    suspend fun getExclusiveNews(): Resource<List<ExclusiveNews>> = suspendCancellableCoroutine { continuation ->
        exclusiveCollectionRef
            .whereEqualTo(Constants.COUNTRY, Session.user.countryCode)
            .whereEqualTo(Constants.ACTIVE_STATUS, true)
            .get().addOnSuccessListener {
                try {
                    Log.e("NewsRepo", Session.user.countryCode.toString())
                    val exclusiveNewsList = mutableListOf<ExclusiveNews>()
                    it.documents.forEach { document ->
                        val exclusiveNews = document.toObject(ExclusiveNews::class.java)?.apply {
                            id = document.id
                        }
                        exclusiveNewsList.add(exclusiveNews!!)
                    }
                    continuation.resume(Resource.Success(exclusiveNewsList))
                } catch (e: Exception) {
                    continuation.resume(Resource.Error(e.message!!, null))
                }
            }.addOnFailureListener {
                continuation.resume(Resource.Error(it.message!!, null))
            }
    }
}