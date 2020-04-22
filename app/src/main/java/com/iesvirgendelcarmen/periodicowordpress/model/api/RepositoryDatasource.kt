package com.iesvirgendelcarmen.periodicowordpress.model.api

interface RepositoryDatasource {
    interface Post {
        fun readAllPosts(callback: PostCallback.ListPosts)
    }

    interface Category {
        fun readCategoryById(id: Int, callback: CategoryCallback.OneCategory)
    }

    interface Media {
        fun readMediaById(id: Int, callback: MediaCallback.OneMedia)
    }

    interface User {
        fun readUserById(id: Int, callback: UserCallback.OneUser)
    }
}