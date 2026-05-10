package com.bitirmeprojesi.lezzetkapisi.CalculatorRetrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CommentClient_Instance {

    private const val BASE_URL="http://192.168.163.102:5758/"

    private val retrofit= Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val commentApi: CommentApi=retrofit.create(CommentApi::class.java)
}