package com.iesvirgendelcarmen.periodicowordpress.viewmodel.wordpress

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iesvirgendelcarmen.periodicowordpress.model.Resource
import com.iesvirgendelcarmen.periodicowordpress.model.api.CategoryCallback
import com.iesvirgendelcarmen.periodicowordpress.model.api.CategoryRepositoryVolley
import com.iesvirgendelcarmen.periodicowordpress.model.api.RepositoryDatasource
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Category

class CategoryViewModel: ViewModel() {

    val categoryLiveData = MutableLiveData<Resource<Category>>()
    val categoryListLiveData = MutableLiveData<Resource<List<Category>>>()

    private val repository: RepositoryDatasource.Category = CategoryRepositoryVolley

    fun getCategoryById(id :Int) {
        repository.readCategoryById(id, object: CategoryCallback.OneCategory {
            override fun onResponse(category: Category) {
                categoryLiveData.value = Resource.success(category)
            }

            override fun onError(message: String) {
                categoryLiveData.value = Resource.error(message, Category(-1, -1, "", "", "", "", "", -1))
            }
        })
    }

    fun getCategoriesForNavigationDrawer() {
        repository.readAllCategories(object: CategoryCallback.ListCategory {
            override fun onResponse(categories: List<Category>) {
                categoryListLiveData.value = Resource.success(categories)
            }

            override fun onError(message: String) {
                categoryListLiveData.value = Resource.error(message, emptyList())
            }

            override fun onLoading() {
                categoryListLiveData.value = Resource.loading(emptyList())
            }

        }, 1, 20)
    }
}