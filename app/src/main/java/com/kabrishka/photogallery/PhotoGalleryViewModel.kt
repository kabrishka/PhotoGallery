package com.kabrishka.photogallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.kabrishka.photogallery.model.GalleryItem

class PhotoGalleryViewModel(private val app: Application) : AndroidViewModel(app) {

    val galleryItemsLiveData: LiveData<List<GalleryItem>>
    private var flickrFetchr = FlickrFetchr()
    private val mutableSearchTerm = MutableLiveData<String>()
    val searchTerm: String
        get() = mutableSearchTerm.value ?: ""

    init {
        mutableSearchTerm.value = QueryPreferences.getStoreQuery(app)
        galleryItemsLiveData =
            mutableSearchTerm.switchMap { searchTerm ->
                if (searchTerm.isBlank()) {
                    flickrFetchr.fetchPhotos()
                } else {
                    flickrFetchr.searchPhotos(searchTerm)
                }
            }
    }

    fun fetchPhotos(query: String = "") {
        QueryPreferences.setStoreQuery(app, query)
        mutableSearchTerm.value = query
    }

    override fun onCleared() {
        super.onCleared()
        flickrFetchr.cancelRequestInFlight()
    }
}