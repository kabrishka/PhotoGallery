package com.kabrishka.photogallery.api

import retrofit2.Call
import retrofit2.http.GET

interface FlickrApi {

    @GET(
        "services/rest/?method=flickr.interestingness.getList"
                + "&api_key=9b06e081ed952a40dd5695bc9d7bcbe2"
                + "&format=json"
                + "&nojsoncallback=1"
                + "&extras=url_s"
    )
    fun fetchPhotos(): Call<FlickrResponse>
}