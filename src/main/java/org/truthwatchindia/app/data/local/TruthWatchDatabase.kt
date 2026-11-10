package org.truthwatchindia.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.truthwatchindia.app.data.local.dao.ArticleCacheDao
import org.truthwatchindia.app.data.local.dao.BookmarkDao
import org.truthwatchindia.app.data.local.entity.BookmarkEntity
import org.truthwatchindia.app.data.local.entity.CachedArticleEntity

@Database(
    entities = [BookmarkEntity::class, CachedArticleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TruthWatchDatabase : RoomDatabase() {

    abstract fun bookmarkDao(): BookmarkDao
    abstract fun articleCacheDao(): ArticleCacheDao

    companion object {
        @Volatile
        private var INSTANCE: TruthWatchDatabase? = null

        fun getInstance(context: Context): TruthWatchDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TruthWatchDatabase::class.java,
                    "truthwatch_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
