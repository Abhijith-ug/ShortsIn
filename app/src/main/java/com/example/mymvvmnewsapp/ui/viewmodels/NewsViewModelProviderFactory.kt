package com.example.mymvvmnewsapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mymvvmnewsapp.repository.NewsRepositoryImpl


class NewsViewModelProviderFactory(
    val app: Application,
    private val newsRepository: NewsRepositoryImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(app, newsRepository) as T
    }
}