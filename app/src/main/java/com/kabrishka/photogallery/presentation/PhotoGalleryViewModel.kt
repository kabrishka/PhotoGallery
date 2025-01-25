package com.kabrishka.photogallery.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.kabrishka.photogallery.data.FlickrFetchr
import com.kabrishka.photogallery.data.QueryPreferences
import com.kabrishka.photogallery.domain.GalleryItem

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