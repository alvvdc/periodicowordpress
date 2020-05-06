package com.iesvirgendelcarmen.periodicowordpress.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iesvirgendelcarmen.periodicowordpress.AppDatabase
import com.iesvirgendelcarmen.periodicowordpress.model.room.Bookmark
import com.iesvirgendelcarmen.periodicowordpress.model.room.BookmarkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookmarkViewModel(application: Application) :AndroidViewModel(application) {
    private val repository: BookmarkRepository

    init {
        val dao = AppDatabase.getDatabase(application).bookmarkDao()
        repository = BookmarkRepository(dao)
    }

    fun getAll() = repository.getAll()

    fun add(bookmark: Bookmark) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(bookmark)
    }

    fun remove(bookmark: Bookmark) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(bookmark)
    }
}