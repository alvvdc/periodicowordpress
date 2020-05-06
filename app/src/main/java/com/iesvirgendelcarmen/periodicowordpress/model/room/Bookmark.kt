package com.iesvirgendelcarmen.periodicowordpress.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Bookmark(
    @PrimaryKey
    val postId: Int


) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Bookmark

        if (postId != other.postId) return false

        return true
    }

    override fun hashCode(): Int {
        return postId
    }
}