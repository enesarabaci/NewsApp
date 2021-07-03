package com.example.newsapp.DependencyInjection

import android.content.Context
import androidx.room.Room
import com.example.newsapp.Api.NewsApi
import com.example.newsapp.Room.NewsDatabase
import com.example.newsapp.Repo.Repository
import com.example.newsapp.Repo.RepositoryInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun getNewsApi() : NewsApi = Retrofit.Builder()
        .baseUrl(NewsApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NewsApi::class.java)

    @Singleton
    @Provides
    fun getDatabase(@ApplicationContext context: Context) : NewsDatabase = Room.databaseBuilder(
        context,
        NewsDatabase::class.java,
        "news_database"
    ).build()

    @Singleton
    @Provides
    fun getRepository(
        newsDB: NewsDatabase,
        newsApi: NewsApi
    ) : RepositoryInterface = Repository(newsDB, newsApi)

}