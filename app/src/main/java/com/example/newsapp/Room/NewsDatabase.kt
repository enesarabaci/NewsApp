package com.example.newsapp.Room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.newsapp.Model.Article
import com.example.newsapp.Model.FavoriteArticle

@Database(entities = [Article::class, FavoriteArticle::class], version = 1)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun getDao() : NewsDao

}