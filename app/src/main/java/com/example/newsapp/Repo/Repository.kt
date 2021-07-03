package com.example.newsapp.Repo

import androidx.paging.*
import androidx.room.withTransaction
import com.example.newsapp.Api.NewsApi
import com.example.newsapp.Model.Article
import com.example.newsapp.Model.ArticlesPagingSource
import com.example.newsapp.Model.FavoriteArticle
import com.example.newsapp.Room.NewsDatabase
import com.example.newsapp.Util.Resource
import com.example.newsapp.Util.networkBoundResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class Repository @Inject constructor(
    private val newsDB: NewsDatabase,
    private val api: NewsApi
) : RepositoryInterface {
    private val dao = newsDB.getDao()

    @ExperimentalCoroutinesApi
    override fun getNews(refresh: Boolean): Flow<Resource<*>> = networkBoundResource(
        query = {
            dao.getNews()
        },
        fetch = {
            api.getHeadLines()
        },
        saveFetchResult = { topHeadlines ->
            val favorites = dao.getFavoriteArticles().first()
            val data = topHeadlines.articles
            data.map { article ->
                article.time = System.currentTimeMillis()
                article.isFavorite = favorites.any { favoriteArticle ->
                    article.url == favoriteArticle.favoriteUrl
                }
            }
            newsDB.withTransaction {
                dao.deleteAllNews()
                dao.insertNews(data)
            }
        },
        shouldFetch = { list ->
            if (refresh || list.isNullOrEmpty()) {
                true
            } else {
                val data = list.first()
                val result =
                    data.time < (System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(15))
                result
            }
        }
    )

    override fun searchNews(
        category: String?,
        country: String,
        query: String?
    ) = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            ArticlesPagingSource(
                api,
                category,
                country,
                query,
                dao
            )
        }
    ).flow

    override suspend fun updateFavorite(article: Article) {
        dao.updateFavoriteNews(article)
    }

    override fun getFavoriteArticle(url: String): Flow<List<FavoriteArticle>> =
        dao.getFavoriteArticle(url)

    override fun getFavorites(): Flow<List<FavoriteArticle>> = dao.getFavoriteArticles()

    override suspend fun deleteFavoriteArticle(url: String) {
        dao.deleteFavoriteArticle(url)
    }

    override suspend fun insertFavoriteArticle(article: FavoriteArticle) {
        dao.insertFavoriteArticle(article)
    }


}