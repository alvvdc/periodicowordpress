package com.iesvirgendelcarmen.periodicowordpress.model.wordpress

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    val id: Int,
    val count: Int,
    val description: String,
    val link: String,
    val name: String,
    val slug: String,
    val taxonomy: String,
    val parent: Int
): Parcelable