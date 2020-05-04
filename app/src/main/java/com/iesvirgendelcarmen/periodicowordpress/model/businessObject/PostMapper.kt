package com.iesvirgendelcarmen.periodicowordpress.model.businessObject

import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Category
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Media
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Post
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.User

class PostMapper {
    companion object {
        fun transformDTOtoBO(post: Post, media: MediaBO, user: User, categories: List<Category>): PostBO {
            return PostBO(
                post.id,
                post.date,
                post.modified,
                post.link,
                post.title,
                post.content,
                user,
                media,
                categories
            )
        }
    }
}