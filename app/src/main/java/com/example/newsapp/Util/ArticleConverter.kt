package com.example.newsapp.Util

import com.example.newsapp.Model.Article
import com.example.newsapp.Model.FavoriteArticle

fun Article.makeFavoriteArticle() : FavoriteArticle =
    FavoriteArticle(
        author,
        content,
        description,
        title,
        url,
        urlToImage
    )

fun FavoriteArticle.makeArticle(favorite: Boolean) : Article =
    Article(
        favoriteAuthor,
        favoriteContent,
        favoriteDescription,
        favoriteTitle,
        favoriteUrl,
        favoriteUrlToImage,
        isFavorite = favorite
    )