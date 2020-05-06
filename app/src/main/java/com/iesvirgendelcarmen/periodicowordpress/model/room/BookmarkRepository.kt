package com.iesvirgendelcarmen.periodicowordpress.model.room

class BookmarkRepository(private val bookmarkDao: BookmarkDao) {

    fun getAll() = bookmarkDao.getAll()
    fun insert(bookmark: Bookmark) = bookmarkDao.insert(bookmark)
    fun delete(bookmark: Bookmark) = bookmarkDao.delete(bookmark)
}