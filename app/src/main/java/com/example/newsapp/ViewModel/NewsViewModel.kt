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
class NewsViewModel @Inject constructor(
    private val repo: RepositoryInterface
) : ViewModel() {

    private val _refresh = Channel<Boolean>()
    private val refresh = _refresh.receiveAsFlow()

    @ExperimentalCoroutinesApi
    val news = refresh.flatMapLatest {
        repo.getNews(it)
    }

    fun refreshing(isRefresh: Boolean = true) {
        viewModelScope.launch {
            _refresh.send(isRefresh)
        }
    }

    override fun onCleared() {
        _refresh.close()
        super.onCleared()
    }

    fun deleteFavoriteArticle(favoriteArticle: FavoriteArticle) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateFavorite(favoriteArticle.makeArticle(true))
            _refresh.send(true)
        }
    }

    fun insertFavoriteArticle(favoriteArticle: FavoriteArticle) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateFavorite(favoriteArticle.makeArticle(false))
            _refresh.send(true)
        }
    }

}