package com.example.newsapp.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.example.newsapp.ArticleWorkManager
import java.util.concurrent.TimeUnit

class MainViewModel() : ViewModel() {

    private var context: Context? = null

    fun viewModelContext(cntxt: Context) {
        if (context == null) {
            context = cntxt
            workManager()
        }
    }

    private fun workManager() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = PeriodicWorkRequest.Builder(
            ArticleWorkManager::class.java,
            15,
            TimeUnit.MINUTES
        ).setConstraints(constraints).build()

        WorkManager.getInstance(context!!).enqueueUniquePeriodicWork(
            "articles",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

}