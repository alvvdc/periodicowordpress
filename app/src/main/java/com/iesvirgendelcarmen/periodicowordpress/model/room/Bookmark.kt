package com.iesvirgendelcarmen.periodicowordpress.model.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Bookmark(
    @PrimaryKey
    val postId: Int
): Parcelable {
    companion object {
        fun convertListToArray(bookmarks: List<Bookmark>): Array<Int> {
            val postsIds = mutableListOf<Int>()
            bookmarks.forEach { bookmark -> postsIds.add(bookmark.postId) }
            return postsIds.toTypedArray()
        }
    }

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