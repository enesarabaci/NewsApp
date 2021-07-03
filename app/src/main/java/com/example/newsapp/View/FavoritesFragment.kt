package com.example.newsapp.View

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.Adapter.FavoritesRecyclerAdapter
import com.example.newsapp.R
import com.example.newsapp.Util.makeFavoriteArticle
import com.example.newsapp.Util.showArticleDialog
import com.example.newsapp.ViewModel.FavoritesViewModel
import com.example.newsapp.databinding.FragmentFavoritesBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private lateinit var binding: FragmentFavoritesBinding
    private val viewModel: FavoritesViewModel by viewModels()
    private val recyclerAdapter = FavoritesRecyclerAdapter()

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentFavoritesBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        viewModel.viewModelFavorite()

        binding.favoritesProgressBar.visibility = View.VISIBLE
        collectData()
        setupRecyclerView()
    }

    @ExperimentalCoroutinesApi
    private fun collectData() {
        lifecycleScope.launchWhenCreated {
            viewModel.favorites.collect { list ->
                recyclerAdapter.list = list
                binding.favoritesProgressBar.visibility = View.GONE
            }
        }
    }

    private fun setupRecyclerView() {
        binding.favoritesRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recyclerAdapter
            setHasFixedSize(true)
            val itemTouchHelper = ItemTouchHelper(simpleCallback)
            itemTouchHelper.attachToRecyclerView(this)
        }
        recyclerAdapter.setOnItemClickListener { article ->
            findNavController().navigate(FavoritesFragmentDirections.actionRecordsFragmentToDetailFragment(article))
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

    private val simpleCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                //val position = viewHolder.bindingAdapterPosition
                val ml = recyclerAdapter.list.toMutableList()
                val favoriteArticle = ml.get(position)
                viewModel.deleteFavoriteArticle(favoriteArticle)
                ml.removeAt(position)
                recyclerAdapter.list = ml.toList()

                Snackbar.make(requireView(), "Silindi", Snackbar.LENGTH_LONG)
                    .setAction("Geri Al") {
                        viewModel.insertFavoriteArticle(favoriteArticle)
                    }
                    .show()
            }

        }

}