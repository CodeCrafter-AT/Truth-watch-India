package org.truthwatchindia.app.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.getTotalChangedRows
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import org.truthwatchindia.app.`data`.local.entity.CachedArticleEntity

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ArticleCacheDao_Impl(
  __db: RoomDatabase,
) : ArticleCacheDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCachedArticleEntity: EntityInsertAdapter<CachedArticleEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfCachedArticleEntity = object : EntityInsertAdapter<CachedArticleEntity>()
        {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `cached_articles` (`postId`,`title`,`content`,`excerpt`,`author`,`date`,`category`,`imageUrl`,`link`,`section`,`cachedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CachedArticleEntity) {
        statement.bindLong(1, entity.postId)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.content)
        statement.bindText(4, entity.excerpt)
        statement.bindText(5, entity.author)
        statement.bindText(6, entity.date)
        statement.bindText(7, entity.category)
        val _tmpImageUrl: String? = entity.imageUrl
        if (_tmpImageUrl == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpImageUrl)
        }
        statement.bindText(9, entity.link)
        statement.bindText(10, entity.section)
        statement.bindLong(11, entity.cachedAt)
      }
    }
  }

  public override suspend fun insertArticles(articles: List<CachedArticleEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCachedArticleEntity.insert(_connection, articles)
  }

  public override fun getArticlesBySection(section: String): Flow<List<CachedArticleEntity>> {
    val _sql: String = "SELECT * FROM cached_articles WHERE section = ? ORDER BY cachedAt DESC"
    return createFlow(__db, false, arrayOf("cached_articles")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, section)
        val _columnIndexOfPostId: Int = getColumnIndexOrThrow(_stmt, "postId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfExcerpt: Int = getColumnIndexOrThrow(_stmt, "excerpt")
        val _columnIndexOfAuthor: Int = getColumnIndexOrThrow(_stmt, "author")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfLink: Int = getColumnIndexOrThrow(_stmt, "link")
        val _columnIndexOfSection: Int = getColumnIndexOrThrow(_stmt, "section")
        val _columnIndexOfCachedAt: Int = getColumnIndexOrThrow(_stmt, "cachedAt")
        val _result: MutableList<CachedArticleEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CachedArticleEntity
          val _tmpPostId: Long
          _tmpPostId = _stmt.getLong(_columnIndexOfPostId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          val _tmpExcerpt: String
          _tmpExcerpt = _stmt.getText(_columnIndexOfExcerpt)
          val _tmpAuthor: String
          _tmpAuthor = _stmt.getText(_columnIndexOfAuthor)
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpLink: String
          _tmpLink = _stmt.getText(_columnIndexOfLink)
          val _tmpSection: String
          _tmpSection = _stmt.getText(_columnIndexOfSection)
          val _tmpCachedAt: Long
          _tmpCachedAt = _stmt.getLong(_columnIndexOfCachedAt)
          _item =
              CachedArticleEntity(_tmpPostId,_tmpTitle,_tmpContent,_tmpExcerpt,_tmpAuthor,_tmpDate,_tmpCategory,_tmpImageUrl,_tmpLink,_tmpSection,_tmpCachedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getArticleById(postId: Long): CachedArticleEntity? {
    val _sql: String = "SELECT * FROM cached_articles WHERE postId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, postId)
        val _columnIndexOfPostId: Int = getColumnIndexOrThrow(_stmt, "postId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfExcerpt: Int = getColumnIndexOrThrow(_stmt, "excerpt")
        val _columnIndexOfAuthor: Int = getColumnIndexOrThrow(_stmt, "author")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfLink: Int = getColumnIndexOrThrow(_stmt, "link")
        val _columnIndexOfSection: Int = getColumnIndexOrThrow(_stmt, "section")
        val _columnIndexOfCachedAt: Int = getColumnIndexOrThrow(_stmt, "cachedAt")
        val _result: CachedArticleEntity?
        if (_stmt.step()) {
          val _tmpPostId: Long
          _tmpPostId = _stmt.getLong(_columnIndexOfPostId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          val _tmpExcerpt: String
          _tmpExcerpt = _stmt.getText(_columnIndexOfExcerpt)
          val _tmpAuthor: String
          _tmpAuthor = _stmt.getText(_columnIndexOfAuthor)
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpLink: String
          _tmpLink = _stmt.getText(_columnIndexOfLink)
          val _tmpSection: String
          _tmpSection = _stmt.getText(_columnIndexOfSection)
          val _tmpCachedAt: Long
          _tmpCachedAt = _stmt.getLong(_columnIndexOfCachedAt)
          _result =
              CachedArticleEntity(_tmpPostId,_tmpTitle,_tmpContent,_tmpExcerpt,_tmpAuthor,_tmpDate,_tmpCategory,_tmpImageUrl,_tmpLink,_tmpSection,_tmpCachedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearSection(section: String): Int {
    val _sql: String = "DELETE FROM cached_articles WHERE section = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, section)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearOldCache(threshold: Long): Int {
    val _sql: String = "DELETE FROM cached_articles WHERE cachedAt < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, threshold)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearAllCache(): Int {
    val _sql: String = "DELETE FROM cached_articles"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
