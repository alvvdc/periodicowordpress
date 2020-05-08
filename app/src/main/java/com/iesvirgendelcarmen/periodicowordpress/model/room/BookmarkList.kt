package com.iesvirgendelcarmen.periodicowordpress.model.room

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BookmarkList(
    val bookmarkList: List<Bookmark>
): Parcelable