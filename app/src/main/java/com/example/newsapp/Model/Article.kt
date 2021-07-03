package com.example.newsapp.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "news_table")
data class Article(
    val author: String?,
    val content: String?,
    val description: String?,
    val title: String,
    @PrimaryKey
    val url: String,
    val urlToImage: String?,
    var time: Long = System.currentTimeMillis(),
    var isFavorite: Boolean = false
) : Serializable