package com.example.newsapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.Model.FavoriteArticle
import com.example.newsapp.Repo.RepositoryInterface
import com.example.newsapp.Util.makeArticle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repo: RepositoryInterface
) : ViewModel() {

    private val _favorite = Channel<Boolean>()
    private var favorite = _favorite.receiveAsFlow()

    @ExperimentalCoroutinesApi
    val favorites = favorite.flatMapLatest {
        repo.getFavorites()
    }

    fun viewModelFavorite() {
        viewModelScope.launch {
            _favorite.send(true)
        }
    }

    fun deleteFavoriteArticle(favoriteArticle: FavoriteArticle) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateFavorite(favoriteArticle.makeArticle(true))
            _favorite.send(false)
        }
    }

    fun insertFavoriteArticle(favoriteArticle: FavoriteArticle) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateFavorite(favoriteArticle.makeArticle(false))
            _favorite.send(true)
        }
    }


}