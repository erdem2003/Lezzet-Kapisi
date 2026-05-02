package com.bitirmeprojesi.lezzetkapisi.Yolo_Category_Detect_Retrofit

import com.bitirmeprojesi.lezzetkapisi.Model.YoloResponse_FCD
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface YoloFCD_Service {
    @Multipart  // binary yapı göndermek için
    @POST("detect_category")
    suspend fun detectCategory(
        @Part image: MultipartBody.Part

    ): Response<YoloResponse_FCD>




}