package com.kabrishka.photogallery.api

import com.google.gson.annotations.SerializedName
import com.kabrishka.photogallery.domain.GalleryItem

class PhotoResponse {
    @SerializedName("photo")
    lateinit var galleryItems: List<GalleryItem>
}