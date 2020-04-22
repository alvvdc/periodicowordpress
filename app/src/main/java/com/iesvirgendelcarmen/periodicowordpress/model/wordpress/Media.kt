package com.iesvirgendelcarmen.periodicowordpress.model.wordpress

import com.google.gson.annotations.SerializedName
import java.util.*

data class Media(
    val id: Int,
    val date: Date,
    val modified: Date,
    val link: String,
    val title: Rendered,
    val author: Int,
    val description: Rendered,
    val caption: Rendered,
    @SerializedName("alt_text")
    val altText: String,
    @SerializedName("media_type")
    val mediaType: String,
    @SerializedName("mime_type")
    val mimeType: String,
    @SerializedName("media_details")
    val mediaDetails: MediaDetails,
    val post: Int,
    @SerializedName("source_url")
    val sourceUrl: String
)