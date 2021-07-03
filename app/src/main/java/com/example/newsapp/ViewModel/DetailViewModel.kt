package com.example.newsapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.Model.Article
import com.example.newsapp.Repo.RepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repo: RepositoryInterface
) : ViewModel() {

    private val _favorite = Channel<Boolean>()
    private var favorite = _favorite.receiveAsFlow()
    var url = ""

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    @ExperimentalCoroutinesApi
    val result = favorite.flatMapLatest {
        repo.getFavoriteArticle(url)
    }

    init {
        viewModelScope.launch {
            result.collect { list ->
                _isFavorite.value = list.isNotEmpty()
            }
        }
    }

    fun updateFavorite(article: Article) {
        viewModelScope.launch {
            repo.updateFavorite(article)
            _favorite.send(!article.isFavorite)
        }
    }

    override fun onCleared() {
        _favorite.close()
        super.onCleared()
    }

}