package com.iesvirgendelcarmen.periodicowordpress.model.businessObject

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.*
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class PostBO(
    var id: Int = -1,
    var date: Date = Date(),
    var modified: Date = Date(),
    var link: String = "",
    var title: Rendered = Rendered(""),
    var content: RenderedProtected = RenderedProtected("", ""),
    var author: User = User(-1, "", "", "", "", AvatarUrls("", "", "")),
    var featuredMedia: Media = Media(-1, Date(), Date(), "", Rendered(""), -1, Rendered(""), Rendered(""), "", "", "", MediaDetails(-1, -1, "", Sizes()), -1, ""),
    var categories: List<Category> = emptyList()
): Parcelable