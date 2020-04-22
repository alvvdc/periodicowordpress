package com.iesvirgendelcarmen.periodicowordpress.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.PostBO
import com.iesvirgendelcarmen.periodicowordpress.model.Resource
import com.iesvirgendelcarmen.periodicowordpress.model.api.*
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.*

class PostBoViewModel(): ViewModel() {

    val postsBoListLiveData = MutableLiveData<Resource<List<PostBO>>>()

    private val postRepository = PostRepositoryVolley
    private val mediaRepository = MediaRepositoryVolley
    private val userRepository = UserRepositoryVolley
    private val categoryRepository = CategoryRepositoryVolley

    fun getAllPostsBo()
    {

        val postsBoList = mutableListOf<PostBO>()

        postRepository.readAllPosts(object: PostCallback.ListPosts
        {
            override fun onResponse(posts: List<Post>)
            {

                for (post in posts)
                {
                    val postBo = PostBO()

                    postBo.title = post.title
                    postBo.date = post.date
                    postBo.modified = post.modified
                    postBo.link = post.link
                    postBo.title = post.title
                    postBo.content = post.content


                    val mediaId = post.featuredMedia
                    if (mediaId != 0)
                    {
                        mediaRepository.readMediaById(mediaId, object: MediaCallback.OneMedia
                        {
                            override fun onResponse(media: Media) {
                                postBo.featuredMedia = media
                            }

                            override fun onError(message: String) {

                            }

                            override fun onLoading() {

                            }

                        })
                    }

                    val authorId = post.author
                    userRepository.readUserById(authorId, object: UserCallback.OneUser
                    {
                        override fun onResponse(user: User) {
                            postBo.author = user
                        }

                        override fun onError(message: String) {

                        }

                        override fun onLoading() {

                        }

                    })


                    val categoriesIds = post.categories
                    val categories = mutableListOf<Category>()

                    for (categoryId in categoriesIds)
                    {
                        categoryRepository.readCategoryById(categoryId, object: CategoryCallback.OneCategory
                        {
                            override fun onResponse(category: Category) {
                                categories.add(category)
                            }

                            override fun onError(message: String) {

                            }

                        })
                    }

                    postBo.categories = categories
                    postsBoList.add(postBo)
                }

                postsBoListLiveData.value = Resource.success(postsBoList)
            }

            override fun onError(message: String) {
                postsBoListLiveData.value = Resource.error(message, emptyList())
            }

            override fun onLoading() {
                postsBoListLiveData.value = Resource.loading(emptyList())
            }
        })
    }
}