package org.truthwatchindia.app.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass
import org.truthwatchindia.app.`data`.local.dao.ArticleCacheDao
import org.truthwatchindia.app.`data`.local.dao.ArticleCacheDao_Impl
import org.truthwatchindia.app.`data`.local.dao.BookmarkDao
import org.truthwatchindia.app.`data`.local.dao.BookmarkDao_Impl

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class TruthWatchDatabase_Impl : TruthWatchDatabase() {
  private val _bookmarkDao: Lazy<BookmarkDao> = lazy {
    BookmarkDao_Impl(this)
  }

  private val _articleCacheDao: Lazy<ArticleCacheDao> = lazy {
    ArticleCacheDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1,
        "bdd7730e4187261f54d158f16ea98708", "ed688218553959a0a18545343bc64630") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `bookmarks` (`postId` INTEGER NOT NULL, `title` TEXT NOT NULL, `content` TEXT NOT NULL, `excerpt` TEXT NOT NULL, `author` TEXT NOT NULL, `date` TEXT NOT NULL, `category` TEXT NOT NULL, `imageUrl` TEXT, `link` TEXT NOT NULL, `bookmarkedAt` INTEGER NOT NULL, PRIMARY KEY(`postId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `cached_articles` (`postId` INTEGER NOT NULL, `title` TEXT NOT NULL, `content` TEXT NOT NULL, `excerpt` TEXT NOT NULL, `author` TEXT NOT NULL, `date` TEXT NOT NULL, `category` TEXT NOT NULL, `imageUrl` TEXT, `link` TEXT NOT NULL, `section` TEXT NOT NULL, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`postId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'bdd7730e4187261f54d158f16ea98708')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `bookmarks`")
        connection.execSQL("DROP TABLE IF EXISTS `cached_articles`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsBookmarks: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBookmarks.put("postId", TableInfo.Column("postId", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarks.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarks.put("content", TableInfo.Column("content", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarks.put("excerpt", TableInfo.Column("excerpt", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarks.put("author", TableInfo.Column("author", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarks.put("date", TableInfo.Column("date", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarks.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarks.put("imageUrl", TableInfo.Column("imageUrl", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarks.put("link", TableInfo.Column("link", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarks.put("bookmarkedAt", TableInfo.Column("bookmarkedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBookmarks: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBookmarks: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoBookmarks: TableInfo = TableInfo("bookmarks", _columnsBookmarks,
            _foreignKeysBookmarks, _indicesBookmarks)
        val _existingBookmarks: TableInfo = read(connection, "bookmarks")
        if (!_infoBookmarks.equals(_existingBookmarks)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |bookmarks(org.truthwatchindia.app.data.local.entity.BookmarkEntity).
              | Expected:
              |""".trimMargin() + _infoBookmarks + """
              |
              | Found:
              |""".trimMargin() + _existingBookmarks)
        }
        val _columnsCachedArticles: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCachedArticles.put("postId", TableInfo.Column("postId", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedArticles.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedArticles.put("content", TableInfo.Column("content", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedArticles.put("excerpt", TableInfo.Column("excerpt", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedArticles.put("author", TableInfo.Column("author", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedArticles.put("date", TableInfo.Column("date", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedArticles.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedArticles.put("imageUrl", TableInfo.Column("imageUrl", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedArticles.put("link", TableInfo.Column("link", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedArticles.put("section", TableInfo.Column("section", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedArticles.put("cachedAt", TableInfo.Column("cachedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCachedArticles: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCachedArticles: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoCachedArticles: TableInfo = TableInfo("cached_articles", _columnsCachedArticles,
            _foreignKeysCachedArticles, _indicesCachedArticles)
        val _existingCachedArticles: TableInfo = read(connection, "cached_articles")
        if (!_infoCachedArticles.equals(_existingCachedArticles)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |cached_articles(org.truthwatchindia.app.data.local.entity.CachedArticleEntity).
              | Expected:
              |""".trimMargin() + _infoCachedArticles + """
              |
              | Found:
              |""".trimMargin() + _existingCachedArticles)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "bookmarks", "cached_articles")
  }

  public override fun clearAllTables() {
    super.performClear(false, "bookmarks", "cached_articles")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(BookmarkDao::class, BookmarkDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ArticleCacheDao::class, ArticleCacheDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun bookmarkDao(): BookmarkDao = _bookmarkDao.value

  public override fun articleCacheDao(): ArticleCacheDao = _articleCacheDao.value
}
