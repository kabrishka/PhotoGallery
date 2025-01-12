package com.kabrishka.photogallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class PhotoGalleryViewModel : ViewModel() {

    private var flickrFetchr = FlickrFetchr()

    val galleryItemsLiveData: LiveData<List<GalleryItem>> = flickrFetchr.fetchPhotos()

    override fun onCleared() {
        super.onCleared()
        flickrFetchr.cancelRequestInFlight()
    }
}