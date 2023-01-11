package com.example.mymvvmnewsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.mymvvmnewsapp.ui.viewmodels.UserViewModel
import com.example.mymvvmnewsapp.ui.viewmodels.UserViewModelProviderFactory
import com.example.mynewsappdemo.R
import com.example.mynewsappdemo.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Instantiate the user repository.
        val viewModelProviderFactory = UserViewModelProviderFactory(application)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[UserViewModel::class.java]
    }
}