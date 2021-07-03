package com.example.newsapp.Room

import androidx.room.*
import com.example.newsapp.Model.Article
import com.example.newsapp.Model.FavoriteArticle
import com.example.newsapp.Util.makeFavoriteArticle
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: List<Article>)

    @Query("SELECT * FROM news_table")
    fun getNews(): Flow<List<Article>>

    @Query("DELETE FROM news_table")
    suspend fun deleteAllNews()

    //@Query("SELECT * FROM favorites_table INNER JOIN news_table ON favoriteUrl = url")
    //fun getFavoriteNews(): Flow<List<Article>>

    @Query("SELECT * FROM favorites_table")
    fun getFavoriteArticles(): Flow<List<FavoriteArticle>>

    suspend fun updateFavoriteNews(article: Article) {
        article.apply {
            if (!isFavorite) {
                val favoriteArticle = makeFavoriteArticle()
                insertFavoriteArticle(favoriteArticle)
            } else {
                deleteFavoriteArticle(url)
            }
            article.isFavorite = !article.isFavorite
            updateArticle(article)
        }
    }

    @Update
    suspend fun updateArticle(article: Article)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteArticle(article: FavoriteArticle)

    @Query("DELETE FROM favorites_table WHERE favoriteUrl = :url")
    suspend fun deleteFavoriteArticle(url: String)

    @Query("SELECT * FROM favorites_table WHERE favoriteUrl = :url")
    fun getFavoriteArticle(url: String) : Flow<List<FavoriteArticle>>

}