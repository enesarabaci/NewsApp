package com.example.newsapp.ViewModel

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.newsapp.Model.FavoriteArticle
import com.example.newsapp.Repo.RepositoryInterface
import com.example.newsapp.Util.makeArticle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val repo: RepositoryInterface,
    state: SavedStateHandle
) : ViewModel() {

    private val _query = state.getLiveData("current_query", "")

    val result = _query.switchMap { q ->
        repo.searchNews(category = null, country = "tr", query = q).asLiveData().cachedIn(viewModelScope)
    }

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }

    fun deleteFavoriteArticle(favoriteArticle: FavoriteArticle) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateFavorite(favoriteArticle.makeArticle(true))
            _query.value = _query.value
        }
    }

    fun insertFavoriteArticle(favoriteArticle: FavoriteArticle) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateFavorite(favoriteArticle.makeArticle(false))
            _query.value = _query.value
        }
    }

}