package com.example.newsapp.View

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.Adapter.ArticleLoadStateAdapter
import com.example.newsapp.Adapter.ArticlesPagingAdapter
import com.example.newsapp.R
import com.example.newsapp.Util.makeFavoriteArticle
import com.example.newsapp.Util.showArticleDialog
import com.example.newsapp.ViewModel.SearchResultViewModel
import com.example.newsapp.databinding.FragmentSearchResultBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

@AndroidEntryPoint
class SearchResultFragment : Fragment(R.layout.fragment_search_result) {

    private lateinit var binding: FragmentSearchResultBinding
    private val viewModel: SearchResultViewModel by viewModels()
    private val recyclerAdapter = ArticlesPagingAdapter()

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSearchResultBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        collectData()
        setupRecyclerView()
        binding.apply {
            fragmentSearchButton.setOnClickListener {
                makeSearch(fragmentSearchText.text.toString())
            }
            fragmentSearchText.setOnEditorActionListener { textView, actionId, keyEvent ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    makeSearch(textView.text.toString())
                }
                true
            }
        }
    }

    private fun makeSearch(query: String) {
        if (query.isNotEmpty()) {
            UIUtil.hideKeyboard(requireActivity())
            binding.fragmentSearchText.clearFocus()
            viewModel.updateQuery(query)
        }
    }

    @ExperimentalCoroutinesApi
    private fun collectData() {
        viewModel.result.observe(viewLifecycleOwner) {
            recyclerAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun setupRecyclerView() {
        binding.fragmentSearchRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recyclerAdapter.withLoadStateHeaderAndFooter(
                ArticleLoadStateAdapter{ recyclerAdapter.retry() },
                ArticleLoadStateAdapter{ recyclerAdapter.retry() }
            )
            setHasFixedSize(true)
        }
        recyclerAdapter.setOnItemClickListener { article ->
            findNavController().navigate(SearchResultFragmentDirections.actionSearchResultFragmentToDetailFragment(article))
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