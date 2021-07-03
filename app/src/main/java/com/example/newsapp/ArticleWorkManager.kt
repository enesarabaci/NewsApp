package com.example.newsapp

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.newsapp.Api.NewsApi
import com.example.newsapp.Model.Article
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlin.random.Random

@HiltWorker
class ArticleWorkManager @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val api: NewsApi
) : Worker(context, workerParameters) {

    private var job: Job? = null

    override fun doWork(): Result {
        getArticle()
        return Result.success()
    }

    private fun getArticle() {
        job = CoroutineScope(Dispatchers.IO).launch {
            val articles = api.getHeadLines().articles
            val position = Random.nextInt(0, articles.size-1)
            createNotification(articles.get(position))
        }
    }

    private fun createNotification(article: Article) {
        val notification = NotificationCompat.Builder(applicationContext, "channel1")
            .setSmallIcon(R.drawable.ic_baseline_search_24)
            .setContentTitle(article.author)
            .setContentText(article.title)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        val notificationManagerCompat = NotificationManagerCompat.from(applicationContext)
        notificationManagerCompat.notify(1, notification)
    }

    override fun onStopped() {
        job?.cancel()
        super.onStopped()
    }

}