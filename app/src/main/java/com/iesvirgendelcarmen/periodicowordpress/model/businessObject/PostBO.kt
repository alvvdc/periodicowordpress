package com.iesvirgendelcarmen.periodicowordpress.model.businessObject

import com.google.gson.annotations.SerializedName
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.*
import java.util.*

data class PostBO(
    var id: Int = -1,
    var date: Date = Date(),
    var modified: Date = Date(),
    var link: String = "",
    var title: Rendered = Rendered(""),
    var content: RenderedProtected = RenderedProtected("", ""),
    var author: Int = -1,
    var featuredMedia: Media = Media(-1, Date(), Date(), "", Rendered(""), -1, Rendered(""), Rendered(""), "", "", "", MediaDetails(-1, -1, "", Sizes()), -1, ""),
    var categories: List<Category> = emptyList()
)