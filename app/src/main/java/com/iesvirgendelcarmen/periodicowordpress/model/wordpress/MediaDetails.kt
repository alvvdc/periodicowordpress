package com.iesvirgendelcarmen.periodicowordpress.model.wordpress

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class MediaDetails(
    val width: Int,
    val height: Int,
    val file: String,
    val sizes: Sizes
): Parcelable

@Parcelize
data class Sizes(
    val full: MediaSize? = null,
    val large: MediaSize? = null,
    @SerializedName("medium_large")
    val mediumLarge: MediaSize? = null,
    val medium: MediaSize? = null,
    val thumbnail: MediaSize? = null
): Parcelable

@Parcelize
class MediaSize(
    val width: Int,
    val height: Int,
    val file: String,
    @SerializedName("mime_type")
    val mimeType: String,
    @SerializedName("source_url")
    val sourceUrl: String
): Parcelable