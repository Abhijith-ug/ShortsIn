package com.example.mymvvmnewsapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.mymvvmnewsapp.database.ArticleDatabase
import com.example.mymvvmnewsapp.repository.NewsRepository
import com.example.mymvvmnewsapp.repository.NewsRepositoryImpl
import com.example.mymvvmnewsapp.repository.SubscriptionRepository
import com.example.mymvvmnewsapp.ui.fragments.ExclusiveNewsFragment
import com.example.mymvvmnewsapp.ui.viewmodels.NewsViewModel
import com.example.mymvvmnewsapp.ui.viewmodels.NewsViewModelProviderFactory
import com.example.mymvvmnewsapp.ui.viewmodels.SubscriptionViewModel
import com.example.mymvvmnewsapp.ui.viewmodels.SubscriptionViewModelProviderFactory
import com.example.mymvvmnewsapp.util.Constants
import com.example.mymvvmnewsapp.util.Constants.Companion.EXCLUSIVE_NEWS_FRAGMENT_TAG
import com.example.mymvvmnewsapp.util.Constants.Companion.IS_NEW_SUBSCRIPTION_TO_EXCLUSIVE_NEWS
import com.example.mynewsappdemo.R
import com.example.mynewsappdemo.databinding.ActivityNewsBinding
import com.google.firebase.auth.FirebaseAuth

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding
    lateinit var viewModel: NewsViewModel
    lateinit var subscriptionViewModel: SubscriptionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get bundle for checking new exclusive news added.
        if (intent.extras?.containsKey(IS_NEW_SUBSCRIPTION_TO_EXCLUSIVE_NEWS) == true) {
            val isNewSubscriptionAddedForExclusiveNews = intent.extras!!.getBoolean(IS_NEW_SUBSCRIPTION_TO_EXCLUSIVE_NEWS)
            if (isNewSubscriptionAddedForExclusiveNews) {
                val exclusiveNewsFragment = ExclusiveNewsFragment()
                bottomNavigationFragmentReplace(exclusiveNewsFragment, EXCLUSIVE_NEWS_FRAGMENT_TAG)
            }
        }

        // Instantiate the news repository.
        val newsRepository = NewsRepositoryImpl(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[NewsViewModel::class.java]

        // Instantiate the news subscription.
        val subscriptionRepository = SubscriptionRepository()
        val subscriptionProviderFactory = SubscriptionViewModelProviderFactory(application, subscriptionRepository)
        subscriptionViewModel = ViewModelProvider(this, subscriptionProviderFactory)[SubscriptionViewModel::class.java]

        // Setup bottom navigation view.
        val newsNavHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = newsNavHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun bottomNavigationFragmentReplace(destinationFragment: Fragment, fragmentTag: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val existingFragment = fragmentManager.findFragmentByTag(fragmentTag)
        if (existingFragment == null) {
            fragmentTransaction.replace(R.id.newsNavHostFragment, destinationFragment, fragmentTag)
            fragmentTransaction.addToBackStack(fragmentTag)
            fragmentTransaction.commit()
        } else {
            fragmentManager.popBackStack(fragmentTag, 0);
        }
    }
}