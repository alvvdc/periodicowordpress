package com.iesvirgendelcarmen.periodicowordpress.model.wordpress

import java.util.*

import com.google.gson.annotations.SerializedName

data class Post(
    val id: Int,
    val date: Date,
    val modified: Date,
    val link: String,
    val title: Rendered,
    val content: RenderedProtected,
    val author: Int,
    @SerializedName("featured_media")
    val featuredMedia: Int,
    val categories: List<Int>
)