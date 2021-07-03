package com.example.newsapp.View

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.newsapp.Model.Article
import com.example.newsapp.R
import com.example.newsapp.ViewModel.DetailViewModel
import com.example.newsapp.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail) {

    private lateinit var binding: FragmentDetailBinding
    @ExperimentalCoroutinesApi
    private val viewModel: DetailViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()
    private lateinit var article: Article

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentDetailBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        article = args.article
        viewModel.url = article.url
        updateFabColor(article.isFavorite)

        binding.apply {
            fragmentDetailWebview.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    fragmentDetailProgressBar.visibility = View.GONE
                    super.onPageFinished(view, url)
                }
            }
            fragmentDetailWebview.loadUrl(article.url)

            fragmentDetailFavoriteButton.setOnClickListener {
                viewModel.updateFavorite(article)
            }
        }

        collectData()
    }

    @ExperimentalCoroutinesApi
    @SuppressLint("ResourceAsColor")
    private fun collectData() {
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            binding.apply {
                updateFabColor(isFavorite)
            }
            article.isFavorite = isFavorite
        }
    }

    private fun updateFabColor(favorite: Boolean) {
        binding.fragmentDetailFavoriteButton.setColorFilter(ContextCompat.getColor(
            requireContext(),
            if (favorite) R.color.red else R.color.black
        ))
    }

}