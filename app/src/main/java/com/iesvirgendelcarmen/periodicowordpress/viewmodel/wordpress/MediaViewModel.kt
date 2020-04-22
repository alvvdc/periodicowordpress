package com.iesvirgendelcarmen.periodicowordpress.viewmodel.wordpress

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iesvirgendelcarmen.periodicowordpress.model.Resource
import com.iesvirgendelcarmen.periodicowordpress.model.api.MediaCallback
import com.iesvirgendelcarmen.periodicowordpress.model.api.MediaRepositoryVolley
import com.iesvirgendelcarmen.periodicowordpress.model.api.RepositoryDatasource
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Media
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.MediaDetails
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Rendered
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Sizes
import java.util.*

class MediaViewModel: ViewModel() {

    val mediaLiveData = MutableLiveData<Resource<Media>>()

    val repository: RepositoryDatasource.Media = MediaRepositoryVolley

    fun getMediaById(id :Int) {
        repository.readMediaById(id, object: MediaCallback.OneMedia {
            override fun onResponse(media: Media) {
                mediaLiveData.value = Resource.success(media)
            }

            override fun onError(message: String) {
                mediaLiveData.value = Resource.error(message, Media(-1, Date(), Date(), "", Rendered(""), -1, Rendered(""), Rendered(""), "", "", "", MediaDetails(-1, -1, "", Sizes()), -1, ""))
            }

            override fun onLoading() {
                mediaLiveData.value = Resource.loading(Media(-1, Date(), Date(), "", Rendered(""), -1, Rendered(""), Rendered(""), "", "", "", MediaDetails(-1, -1, "", Sizes()), -1, ""))
            }
        })
    }
}