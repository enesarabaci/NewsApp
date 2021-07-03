package com.example.newsapp.Model

data class TopHeadlines(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)