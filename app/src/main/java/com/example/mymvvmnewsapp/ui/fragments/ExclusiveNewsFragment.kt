package com.example.mymvvmnewsapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymvvmnewsapp.adapters.ExclusiveNewsAdapter
import com.example.mymvvmnewsapp.models.Article
import com.example.mymvvmnewsapp.models.ExclusiveNews
import com.example.mymvvmnewsapp.models.Source
import com.example.mymvvmnewsapp.ui.NewsActivity
import com.example.mymvvmnewsapp.ui.NewsSubscriptionActivity
import com.example.mymvvmnewsapp.ui.viewmodels.NewsViewModel
import com.example.mymvvmnewsapp.ui.viewmodels.SubscriptionViewModel
import com.example.mymvvmnewsapp.util.UiState
import com.example.mynewsappdemo.R
import com.example.mynewsappdemo.databinding.FragmentBreakingNewsBinding
import com.example.mynewsappdemo.databinding.FragmentExclusiveNewsBinding

class ExclusiveNewsFragment : Fragment(R.layout.fragment_exclusive_news) {

    private lateinit var binding: FragmentExclusiveNewsBinding
    private lateinit var exclusiveNewsAdapter: ExclusiveNewsAdapter
    private lateinit var viewModel: NewsViewModel
    private lateinit var subscriptionViewModel: SubscriptionViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentExclusiveNewsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        subscriptionViewModel = (activity as NewsActivity).subscriptionViewModel

        setupRecyclerView()

        exclusiveNewsAdapter.setOnItemClickListener {
            // Get the clicked the article and put into a bundle and attach the bundle to a navigation component.

            val bundle = Bundle().apply {
                putSerializable("exclusive_news", it)
            }
            // Fragment transition.
            findNavController().navigate(
                R.id.action_exclusiveNewsFragment_to_exclusiveNewsViewFragment, bundle
            )
        }
        checkIsSubscriptionExist()
    }

    override fun onStart() {
        super.onStart()
        subscriptionViewModel.checkUserHaveAnyActiveSubscription {
            when (it) {
                is UiState.Failure -> {
                    Toast.makeText(activity, "Couldn't complete request, please try again", Toast.LENGTH_SHORT).show()
                }
                UiState.Loading -> {}
                is UiState.Success -> {
                    if (it.data) {
                        viewModel.getExclusiveNews()
                        viewModel.exclusiveNewsFromFireStore.observe(viewLifecycleOwner, Observer { exclusiveNews ->
                            exclusiveNewsAdapter.differ.submitList(exclusiveNews)
                        })
                    }
                }
            }
        }
    }

    private fun checkIsSubscriptionExist() {
        subscriptionViewModel.checkUserHaveAnyActiveSubscription {
            when (it) {
                is UiState.Failure -> {
                    Toast.makeText(activity, "Couldn't complete request, please try again", Toast.LENGTH_SHORT).show()
                }
                UiState.Loading -> {}
                is UiState.Success -> {
                    if (it.data) {
                        viewModel.getExclusiveNews()
                        viewModel.exclusiveNewsFromFireStore.observe(viewLifecycleOwner, Observer { exclusiveNews ->
                            exclusiveNewsAdapter.differ.submitList(exclusiveNews)
                            binding.llNoSearchResult.visibility = View.GONE
                        })
                    } else {
                        startActivity(Intent(activity, NewsSubscriptionActivity::class.java))
                        binding.llNoSearchResult.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun getExclusiveNewsToArticle(exclusiveNews: ExclusiveNews): Article {
        return Article(
            1,
            exclusiveNews.author,
            exclusiveNews.content,
            exclusiveNews.description,
            exclusiveNews.publishedAt,
            Source("", exclusiveNews.source),
            exclusiveNews.title,
            exclusiveNews.url,
            exclusiveNews.urlToImage
        )
    }

    private fun setupRecyclerView() {
        exclusiveNewsAdapter = ExclusiveNewsAdapter()
        binding.rvExclusiveNews.apply {
            adapter = exclusiveNewsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}