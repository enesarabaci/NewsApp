package com.example.newsapp.Repo

import androidx.paging.PagingData
import com.example.newsapp.Model.Article
import com.example.newsapp.Model.FavoriteArticle
import com.example.newsapp.Util.Resource
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {

    fun getNews(refresh: Boolean): Flow<Resource<*>>

    fun searchNews(
        category: String?,
        country: String,
        query: String? = null
    ): Flow<PagingData<Article>>

    suspend fun updateFavorite(article: Article)

    fun getFavoriteArticle(url: String) : Flow<List<FavoriteArticle>>

    fun getFavorites() : Flow<List<FavoriteArticle>>

    suspend fun deleteFavoriteArticle(url: String)

    suspend fun insertFavoriteArticle(article: FavoriteArticle)
}