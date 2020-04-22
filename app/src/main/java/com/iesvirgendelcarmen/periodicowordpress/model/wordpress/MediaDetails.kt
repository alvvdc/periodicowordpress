package com.iesvirgendelcarmen.periodicowordpress.model.wordpress

import com.google.gson.annotations.SerializedName

class MediaDetails(
    width: Int,
    height: Int,
    file: String,
    val sizes: Sizes
): MediaInfo(width, height, file)

data class Sizes(
    val full: MediaSize? = null,
    val large: MediaSize? = null,
    @SerializedName("medium_large")
    val mediumLarge: MediaSize? = null,
    val medium: MediaSize? = null,
    val thumbnail: MediaSize? = null
)

class MediaSize(
    width: Int,
    height: Int,
    file: String,
    @SerializedName("mime_type")
    val mimeType: String,
    @SerializedName("source_url")
    val sourceUrl: String
): MediaInfo(width, height, file)

open class MediaInfo(
    val width: Int,
    val height: Int,
    val file: String
)

