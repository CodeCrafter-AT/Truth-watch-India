package org.truthwatchindia.app

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import okhttp3.OkHttpClient
import org.truthwatchindia.app.data.api.RetrofitClient
import org.truthwatchindia.app.data.local.TruthWatchDatabase
import org.truthwatchindia.app.data.preferences.UserPreferencesRepository
import org.truthwatchindia.app.data.repository.NewsRepositoryImpl
import org.truthwatchindia.app.domain.repository.NewsRepository
import org.truthwatchindia.app.notifications.NotificationHelper
import org.truthwatchindia.app.worker.NewsSyncWorker
import java.util.concurrent.TimeUnit

class TruthWatchApp : Application(), ImageLoaderFactory {

    val database: TruthWatchDatabase by lazy {
        TruthWatchDatabase.getInstance(this)
    }

    val preferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(this)
    }

    val repository: NewsRepository by lazy {
        NewsRepositoryImpl(
            api = RetrofitClient.api,
            bookmarkDao = database.bookmarkDao(),
            articleCacheDao = database.articleCacheDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize background sync & notifications
        NotificationHelper.createNotificationChannel(this)
        NewsSyncWorker.schedulePeriodicSync(this)
    }

    companion object {
        lateinit var instance: TruthWatchApp
            private set
    }

    override fun newImageLoader(): ImageLoader {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        return ImageLoader.Builder(this)
            .okHttpClient(okHttpClient)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(100L * 1024 * 1024) // 100 MB
                    .build()
            }
            .crossfade(true)
            .respectCacheHeaders(false)
            .build()
    }
}
