package com.bitirmeprojesi.lezzetkapisi.Yolo_Category_Detect_Retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object YoloFCD_Instance { // HTTP client nesnesi olusturuyoruz
    private const val BASE_URL = "http://10.37.164.102:5757/"

    val yoloApi: YoloFCD_Service by lazy { // Nesne ilk kullanıldıgında çalışır.
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YoloFCD_Service::class.java) // Bu servisteki endpointleri kullanabilirsin diyoruz.
    }



}