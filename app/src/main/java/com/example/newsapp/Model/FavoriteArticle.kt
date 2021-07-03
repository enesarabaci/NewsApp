package com.example.newsapp.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites_table")
data class FavoriteArticle(
    val favoriteAuthor: String?,
    val favoriteContent: String?,
    val favoriteDescription: String?,
    val favoriteTitle: String,
    @PrimaryKey
    val favoriteUrl: String,
    val favoriteUrlToImage: String?,
)