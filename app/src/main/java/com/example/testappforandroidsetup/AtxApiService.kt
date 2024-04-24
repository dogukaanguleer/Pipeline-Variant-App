package com.example.testappforandroidsetup

import retrofit2.Call
import retrofit2.http.GET

interface AtxApiService {

    @GET("/") // Ana URL'e istek gönder
    fun fetchContent(): Call<String>
}