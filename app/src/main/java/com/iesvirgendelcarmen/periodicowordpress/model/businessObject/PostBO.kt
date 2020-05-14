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
    var featuredMedia: MediaBO = MediaBO(-1, Date(), Date(), "", Rendered(""), User(-1, "", "", "", "", AvatarUrls("", "", "")), Rendered(""), Rendered(""), "", "", "", MediaDetails(-1, -1, "", Sizes()), -1, ""),
    var categories: List<Category> = emptyList()
): Parcelable
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PostBO

        if (id != other.id) return false
        if (date != other.date) return false
        if (modified != other.modified) return false
        if (link != other.link) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + date.hashCode()
        result = 31 * result + modified.hashCode()
        result = 31 * result + link.hashCode()
        return result
    }
}