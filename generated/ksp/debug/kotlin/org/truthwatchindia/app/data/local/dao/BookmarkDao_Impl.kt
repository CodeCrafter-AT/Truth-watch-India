package org.truthwatchindia.app.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.getTotalChangedRows
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import org.truthwatchindia.app.`data`.local.entity.BookmarkEntity

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class BookmarkDao_Impl(
  __db: RoomDatabase,
) : BookmarkDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBookmarkEntity: EntityInsertAdapter<BookmarkEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfBookmarkEntity = object : EntityInsertAdapter<BookmarkEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `bookmarks` (`postId`,`title`,`content`,`excerpt`,`author`,`date`,`category`,`imageUrl`,`link`,`bookmarkedAt`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BookmarkEntity) {
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
        statement.bindLong(10, entity.bookmarkedAt)
      }
    }
  }

  public override suspend fun insertBookmark(bookmark: BookmarkEntity): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfBookmarkEntity.insertAndReturnId(_connection, bookmark)
    _result
  }

  public override fun getAllBookmarks(): Flow<List<BookmarkEntity>> {
    val _sql: String = "SELECT * FROM bookmarks ORDER BY bookmarkedAt DESC"
    return createFlow(__db, false, arrayOf("bookmarks")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfPostId: Int = getColumnIndexOrThrow(_stmt, "postId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfExcerpt: Int = getColumnIndexOrThrow(_stmt, "excerpt")
        val _columnIndexOfAuthor: Int = getColumnIndexOrThrow(_stmt, "author")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfLink: Int = getColumnIndexOrThrow(_stmt, "link")
        val _columnIndexOfBookmarkedAt: Int = getColumnIndexOrThrow(_stmt, "bookmarkedAt")
        val _result: MutableList<BookmarkEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BookmarkEntity
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
          val _tmpBookmarkedAt: Long
          _tmpBookmarkedAt = _stmt.getLong(_columnIndexOfBookmarkedAt)
          _item =
              BookmarkEntity(_tmpPostId,_tmpTitle,_tmpContent,_tmpExcerpt,_tmpAuthor,_tmpDate,_tmpCategory,_tmpImageUrl,_tmpLink,_tmpBookmarkedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun isBookmarked(postId: Long): Flow<Boolean> {
    val _sql: String = "SELECT EXISTS(SELECT 1 FROM bookmarks WHERE postId = ?)"
    return createFlow(__db, false, arrayOf("bookmarks")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, postId)
        val _result: Boolean
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp != 0
        } else {
          _result = false
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun isBookmarkedSync(postId: Long): Boolean {
    val _sql: String = "SELECT EXISTS(SELECT 1 FROM bookmarks WHERE postId = ?)"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, postId)
        val _result: Boolean
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp != 0
        } else {
          _result = false
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getBookmarkById(postId: Long): BookmarkEntity? {
    val _sql: String = "SELECT * FROM bookmarks WHERE postId = ? LIMIT 1"
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
        val _columnIndexOfBookmarkedAt: Int = getColumnIndexOrThrow(_stmt, "bookmarkedAt")
        val _result: BookmarkEntity?
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
          val _tmpBookmarkedAt: Long
          _tmpBookmarkedAt = _stmt.getLong(_columnIndexOfBookmarkedAt)
          _result =
              BookmarkEntity(_tmpPostId,_tmpTitle,_tmpContent,_tmpExcerpt,_tmpAuthor,_tmpDate,_tmpCategory,_tmpImageUrl,_tmpLink,_tmpBookmarkedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteBookmark(postId: Long): Int {
    val _sql: String = "DELETE FROM bookmarks WHERE postId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, postId)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearAllBookmarks(): Int {
    val _sql: String = "DELETE FROM bookmarks"
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
