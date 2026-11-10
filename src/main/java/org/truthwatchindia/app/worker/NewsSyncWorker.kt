package org.truthwatchindia.app.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import org.truthwatchindia.app.TruthWatchApp
import org.truthwatchindia.app.notifications.NotificationHelper
import java.util.concurrent.TimeUnit

class NewsSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as? TruthWatchApp ?: return Result.failure()
        val prefsRepo = app.preferencesRepository
        val repository = app.repository

        // Respect user's push notification setting
        val notificationsEnabled = prefsRepo.notificationsEnabled.first()
        if (!notificationsEnabled) {
            return Result.success()
        }

        return try {
            val result = repository.getPosts(page = 1, perPage = 5)
            if (result.isSuccess) {
                val posts = result.getOrNull().orEmpty()
                if (posts.isNotEmpty()) {
                    // Cache the synced posts
                    repository.cacheArticles("background_sync", posts)

                    val prefs = applicationContext.getSharedPreferences("truth_watch_sync", Context.MODE_PRIVATE)
                    val lastNotifiedId = prefs.getLong("last_notified_post_id", -1L)
                    val latestPost = posts.first()

                    // If a newer post has arrived and we already had a baseline, notify
                    if (lastNotifiedId > 0 && latestPost.id > lastNotifiedId) {
                        NotificationHelper.showNewsNotification(
                            context = applicationContext,
                            postId = latestPost.id,
                            title = latestPost.getCleanTitle(),
                            excerpt = latestPost.getCleanExcerpt()
                        )
                    }

                    // Record latest post ID as notified/seen
                    prefs.edit().putLong("last_notified_post_id", latestPost.id).apply()
                }
                Result.success()
            } else {
                Result.retry()
            }
        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val SYNC_WORK_NAME = "TruthWatchNewsSyncWork"

        fun schedulePeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<NewsSyncWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
        }
    }
}
