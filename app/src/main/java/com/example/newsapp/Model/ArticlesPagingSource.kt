package com.example.newsapp.Model

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsapp.Api.NewsApi
import com.example.newsapp.Room.NewsDao
import kotlinx.coroutines.flow.first

class ArticlesPagingSource(
    private val api: NewsApi,
    private val category: String?,
    private val country: String,
    private val query: String? = null,
    private val dao: NewsDao
) : PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val position = params.key ?: STARTING_INDEX
        return try {
            val response = api.searchHeadlines(category, country, position, query = query)
            if (response.isSuccessful && response.body() != null) {
                val articles = response.body()!!.articles
                val favorites = dao.getFavoriteArticles().first()

                articles.map { article ->
                    article.isFavorite = favorites.any { favoriteArticle ->
                        article.url == favoriteArticle.favoriteUrl
                    }
                }
                LoadResult.Page(
                    data = articles,
                    prevKey = if (position == STARTING_INDEX) null else position-1,
                    nextKey = if (articles.isEmpty()) null else position+1
                )
            } else {
                LoadResult.Error(Throwable(response.message()))
            }
        }catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        const val STARTING_INDEX = 1
    }
}