package com.iesvirgendelcarmen.periodicowordpress.model.businessObject

import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Media
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.User

class MediaMapper {
    companion object {
        fun transformDTOtoBO(media: Media, user: User): MediaBO {
            return MediaBO(
                media.id,
                media.date,
                media.modified,
                media.link,
                media.title,
                user,
                media.description,
                media.caption,
                media.altText,
                media.mediaType,
                media.mimeType,
                media.mediaDetails,
                media.post,
                media.sourceUrl
            )
        }
    }
}