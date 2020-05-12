package com.iesvirgendelcarmen.periodicowordpress.viewmodel

import androidx.lifecycle.ViewModel

class PaginationViewModel: ViewModel() {
    val paginationStatus = PaginationStatus()
}

data class PaginationStatus(var isLoading: Boolean = false, var page: Int = 0, var isListEnded: Boolean = false, var category: Int = -1) {
    fun nextPage() = page++

    fun reset() {
        isLoading = false
        page = 0
        isListEnded = false
        category = -1
    }
}