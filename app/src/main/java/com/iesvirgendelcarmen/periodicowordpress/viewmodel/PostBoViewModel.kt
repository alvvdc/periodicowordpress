package com.iesvirgendelcarmen.periodicowordpress.viewmodel

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

    fun getAllPostsBO()
    {
        loadPosts(object: Request {
            override fun onResponse(postsBO: List<PostBO>) {
                postsBoListLiveData.value = Resource.success(postsBO)
            }

            override fun onError(message: String) {
                postsBoListLiveData.value = Resource.error(message, emptyList())
            }

            override fun onLoading() {
                postsBoListLiveData.value = Resource.loading(emptyList())
            }
        })
    }

    private fun loadPosts(callback: Request)
    {
        callback.onLoading()

        val postsBoList = mutableListOf<PostBO>()

        postRepository.readAllPosts(object: PostCallback.ListPosts
        {
            override fun onResponse(posts: List<Post>)
            {

                for ((index, post) in posts.withIndex())
                {
                    val postBo = PostBO()

                    postBo.title = post.title
                    postBo.date = post.date
                    postBo.modified = post.modified
                    postBo.link = post.link
                    postBo.title = post.title
                    postBo.content = post.content


                    fun addNewPostBO() {
                        postsBoList.add(postBo)

                        if (index >= posts.size - 1)
                            callback.onResponse(postsBoList)
                        else if (postsBoList.isNullOrEmpty())
                            callback.onError("Error loading posts")
                    }



                    val categoriesIds = post.categories
                    val categories = mutableListOf<Category>()

                    fun loadCategories() {
                        for ((index, categoryId) in categoriesIds.withIndex())
                        {

                            categoryRepository.readCategoryById(categoryId, object: CategoryCallback.OneCategory
                            {
                                override fun onResponse(category: Category) {
                                    categories.add(category)

                                    if (index >= categoriesIds.size - 1)
                                        addNewPostBO()
                                }

                                override fun onError(message: String) {

                                }

                            })
                        }
                    }

                    postBo.categories = categories



                    val authorId = post.author
                    fun loadAuthor()
                    {
                        userRepository.readUserById(authorId, object: UserCallback.OneUser
                        {
                            override fun onResponse(user: User) {
                                postBo.author = user
                                loadCategories()
                            }

                            override fun onError(message: String) {

                            }

                            override fun onLoading() {

                            }

                        })
                    }

                    val mediaId = post.featuredMedia
                    if (mediaId != 0)
                    {
                        mediaRepository.readMediaById(mediaId, object: MediaCallback.OneMedia
                        {
                            override fun onResponse(media: Media) {
                                postBo.featuredMedia = media
                                loadAuthor()
                            }

                            override fun onError(message: String) {

                            }

                            override fun onLoading() {

                            }

                        })
                    }
                }
            }

            override fun onError(message: String) {
                postsBoListLiveData.value = Resource.error(message, emptyList())
            }

            override fun onLoading() {
                postsBoListLiveData.value = Resource.loading(emptyList())
            }
        })
    }

    private interface Request {
        fun onResponse(postsBO: List<PostBO>)
        fun onError(message: String)
        fun onLoading()
    }
}