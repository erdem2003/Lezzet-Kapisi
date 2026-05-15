package com.bitirmeprojesi.lezzetkapisi.CalculatorRetrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CalculatorClient_Instance {

    private const val BASE_URL="http://10.37.164.102:5755/"

    private val retrofit= Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val calculatorBusinessApi: CalculatorBusinessApi=retrofit.create(CalculatorBusinessApi::class.java)
}
