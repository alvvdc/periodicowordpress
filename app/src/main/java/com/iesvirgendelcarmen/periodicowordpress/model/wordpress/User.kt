package com.iesvirgendelcarmen.periodicowordpress.model.wordpress

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: Int,
    val name: String,
    val url: String,
    val description: String,
    val slug: String,
    @SerializedName("avatar_urls")
    val avatarUrls: AvatarUrls
): Parcelable

@Parcelize
data class AvatarUrls(
    @SerializedName("24")
    val size24: String,
    @SerializedName("48")
    val size48: String,
    @SerializedName("96")
    val size96: String
): Parcelable