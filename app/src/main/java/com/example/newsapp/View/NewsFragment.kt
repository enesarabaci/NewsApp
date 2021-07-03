package com.example.newsapp.View

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.Adapter.ArticlesRecyclerAdapter
import com.example.newsapp.Model.Article
import com.example.newsapp.R
import com.example.newsapp.Util.Resource
import com.example.newsapp.Util.makeFavoriteArticle
import com.example.newsapp.Util.showArticleDialog
import com.example.newsapp.ViewModel.NewsViewModel
import com.example.newsapp.databinding.FragmentNewsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class NewsFragment : Fragment(R.layout.fragment_news) {

    private lateinit var binding: FragmentNewsBinding
    private val viewModel: NewsViewModel by viewModels()
    private val recyclerAdapter = ArticlesRecyclerAdapter()

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentNewsBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        viewModel.refreshing(false)
        collectData()
        setupRecyclerView()
        binding.newsSrl.setOnRefreshListener {
            viewModel.refreshing()
        }
    }

    @ExperimentalCoroutinesApi
    private fun collectData() {
        lifecycleScope.launchWhenCreated {
            viewModel.news.collect { resource ->
                binding.apply {
                    when (resource) {
                        is Resource.Success -> {
                            stopLoading()
                            val data = resource.data as List<Article>
                            recyclerAdapter.list = data
                        }
                        is Resource.Error -> {
                            stopLoading()
                            val data = resource.data as List<Article>?
                            val message = resource.errorMessage!!
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            data?.let {
                                recyclerAdapter.list = it
                            }
                        }
                        is Resource.Loading -> {
                            if (!newsSrl.isRefreshing) {
                                newsProgressBar.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.newsRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recyclerAdapter
            setHasFixedSize(true)
        }
        recyclerAdapter.setOnItemClickListener { article ->
            findNavController().navigate(NewsFragmentDirections.actionNewsFragmentToDetailFragment(article))
        }
        recyclerAdapter.setOnItemLongClickListener { article ->
            showArticleDialog(article, requireContext()) { article ->
                if (article.isFavorite) {
                    viewModel.insertFavoriteArticle(article.makeFavoriteArticle())
                }else {
                    viewModel.deleteFavoriteArticle(article.makeFavoriteArticle())
                }
            }
        }
    }

    private fun stopLoading() {
        binding.apply {
            newsProgressBar.visibility = View.GONE
            newsSrl.isRefreshing = false
        }
    }

}