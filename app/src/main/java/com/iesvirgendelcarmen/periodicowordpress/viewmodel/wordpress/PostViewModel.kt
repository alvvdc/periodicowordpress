package com.iesvirgendelcarmen.periodicowordpress.viewmodel.wordpress

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iesvirgendelcarmen.periodicowordpress.model.Resource
import com.iesvirgendelcarmen.periodicowordpress.model.api.PostCallback
import com.iesvirgendelcarmen.periodicowordpress.model.api.PostRepositoryVolley
import com.iesvirgendelcarmen.periodicowordpress.model.api.RepositoryDatasource
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Post

class PostViewModel: ViewModel() {

    val postsListLiveData = MutableLiveData<Resource<List<Post>>>()

    private val repository: RepositoryDatasource.Post = PostRepositoryVolley

    fun getAllPosts() {
        repository.readAllPosts(object: PostCallback.ListPosts {
            override fun onResponse(posts: List<Post>) {
                postsListLiveData.value = Resource.success(posts)
            }

            override fun onError(message: String) {
                postsListLiveData.value = Resource.error(message, emptyList())
            }

            override fun onLoading() {
                postsListLiveData.value = Resource.loading(emptyList())
            }

        })
    }
}