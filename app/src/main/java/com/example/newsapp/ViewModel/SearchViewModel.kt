package com.example.newsapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.newsapp.Model.FavoriteArticle
import com.example.newsapp.Repo.RepositoryInterface
import com.example.newsapp.Util.makeArticle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: RepositoryInterface
) : ViewModel() {

    private val _country = MutableStateFlow("tr")
    val country: StateFlow<String> = _country
    private val _category = MutableStateFlow("business")
    val category: StateFlow<String> = _category

    @ExperimentalCoroutinesApi
    val result = combine(_category, _country) { category, country ->
        Pair(category, country)
    }.flatMapLatest { (category, country) ->
        repo.searchNews(category, country).cachedIn(viewModelScope)
    }

    fun updateCountry(newCountry: String) {
        _country.value = newCountry
    }
    fun updateCategory(newCategory: String) {
        _category.value = newCategory
    }

    fun deleteFavoriteArticle(favoriteArticle: FavoriteArticle) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateFavorite(favoriteArticle.makeArticle(true))
            _country.value = country.value
            _category.value = category.value
        }
    }

    fun insertFavoriteArticle(favoriteArticle: FavoriteArticle) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateFavorite(favoriteArticle.makeArticle(false))
            _country.value = country.value
            _category.value = category.value
        }
    }


}