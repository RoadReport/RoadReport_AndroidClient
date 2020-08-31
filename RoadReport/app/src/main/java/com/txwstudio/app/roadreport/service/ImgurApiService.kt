package com.txwstudio.app.roadreport.service

import com.txwstudio.app.roadreport.json.imgurupload.ImgurUploadJson
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://api.imgur.com"

private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .readTimeout(15, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder().client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface ImgurApiService {

    /**
     * Imgur upload API.
     *
     * @param clientId Imgur Client ID
     * @param image The image want to upload
     */
    @Multipart
    @POST("/3/upload")
    fun postImage(
        @Header("Authorization") clientId: String,
        @Part image: MultipartBody.Part
    ):
            Call<ImgurUploadJson>
}

object ImgurApi {
    val retrofitService: ImgurApiService by lazy {
        retrofit.create(ImgurApiService::class.java)
    }
}