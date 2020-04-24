package com.iesvirgendelcarmen.periodicowordpress.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.PostBO
import com.iesvirgendelcarmen.periodicowordpress.model.Resource
import com.iesvirgendelcarmen.periodicowordpress.model.api.*
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.*

class PostBoViewModel(): ViewModel() {

    val postsBoListLiveData = MutableLiveData<Resource<List<PostBO>>>()
    val postBoLiveData = MutableLiveData<Resource<PostBO>>()

    private val postBoRepository = PostBoRepositoryVolley

    fun getPostBoById(id: Int) {

        postBoRepository.readPostBoById(id, object: PostBoCallback.OnePostBO {
            override fun onResponse(postBO: PostBO) {
                postBoLiveData.value = Resource.success(postBO)
            }

            override fun onError(message: String) {
                postBoLiveData.value = Resource.error(message, PostBO())
            }

            override fun onLoading() {
                postBoLiveData.value = Resource.loading(PostBO())
            }

        })
    }

    fun getPosts(page: Int = 1) {

        postBoRepository.readPostsBo(object: PostBoCallback.ListPostBO {
            override fun onResponse(postsBO: List<PostBO>) {
                postsBoListLiveData.value = Resource.success(postsBO)
            }

            override fun onError(message: String) {
                postsBoListLiveData.value = Resource.error(message, emptyList())
            }

            override fun onLoading() {
                postsBoListLiveData.value = Resource.loading(emptyList())
            }

        }, page)
    }
}