package com.example.newsapp.View

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.Adapter.ArticleLoadStateAdapter
import com.example.newsapp.Adapter.ArticlesPagingAdapter
import com.example.newsapp.R
import com.example.newsapp.Util.Util.CATEGORIES
import com.example.newsapp.Util.Util.COUNTRIES
import com.example.newsapp.Util.makeFavoriteArticle
import com.example.newsapp.Util.showArticleDialog
import com.example.newsapp.ViewModel.SearchViewModel
import com.example.newsapp.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModels()
    private val recyclerAdapter = ArticlesPagingAdapter()

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSearchBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        binding.searchProgressBar.visibility = View.VISIBLE
        collectData()
        setupRecyclerView()

        binding.searchFab.setOnClickListener {
            findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToSearchResultFragment())
        }
    }

    override fun onResume() {
        super.onResume()

        val categories = resources.getStringArray(R.array.categories)
        val countries = resources.getStringArray(R.array.countries)
        val categoriesArrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, categories)
        val countriesArrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, countries)
        binding.apply {
            val categoryIndex = CATEGORIES.indexOf(viewModel.category.value)
            val countryIndex = COUNTRIES.indexOf(viewModel.country.value)
            categoriesActv.setText(categories.get(categoryIndex))
            countriesActv.setText(countries[countryIndex])
            categoriesActv.setAdapter(categoriesArrayAdapter)
            countriesActv.setAdapter(countriesArrayAdapter)

            categoriesActv.setOnItemClickListener { adapterView, view, position, l ->
                if (CATEGORIES.indexOf(viewModel.category.value) != position) {
                    searchProgressBar.visibility = View.VISIBLE
                    viewModel.updateCategory(CATEGORIES.get(position))
                }
            }
            countriesActv.setOnItemClickListener { adapterView, view, position, l ->
                if (COUNTRIES.indexOf(viewModel.country.value) != position) {
                    searchProgressBar.visibility = View.VISIBLE
                    viewModel.updateCountry(COUNTRIES.get(position))
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun collectData() {
        lifecycleScope.launchWhenCreated {
            viewModel.result.collect {
                recyclerAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                binding.searchProgressBar.visibility = View.GONE
            }
        }
    }

    private fun setupRecyclerView() {
        binding.searchRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recyclerAdapter.withLoadStateHeaderAndFooter(
                ArticleLoadStateAdapter{ recyclerAdapter.retry() },
                ArticleLoadStateAdapter{ recyclerAdapter.retry() }
            )
            setHasFixedSize(true)
        }
        recyclerAdapter.setOnItemClickListener { article ->
            findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToDetailFragment(article))
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

}