package com.iesvirgendelcarmen.periodicowordpress.viewmodel.wordpress

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iesvirgendelcarmen.periodicowordpress.model.Resource
import com.iesvirgendelcarmen.periodicowordpress.model.api.CategoryCallback
import com.iesvirgendelcarmen.periodicowordpress.model.api.CategoryRepository
import com.iesvirgendelcarmen.periodicowordpress.model.api.RepositoryDatasource
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Category

class CategoryViewModel: ViewModel() {

    val categoryLiveData = MutableLiveData<Resource<Category>>()

    private val repository: RepositoryDatasource.Category = CategoryRepository

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
}