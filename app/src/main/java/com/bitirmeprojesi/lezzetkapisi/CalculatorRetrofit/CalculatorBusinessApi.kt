package com.bitirmeprojesi.lezzetkapisi.CalculatorRetrofit

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CalculatorBusinessApi {

    @POST("comments/send_comments")
    suspend fun sendMessage(
        @Body message: CommentBusinessRequest
    ): CommentBusinessResponse

    @POST("business/send_star")
    suspend fun sendStar(
        @Body request: StarBusinessRequest
    ): Response<StarBusinessResponse>
}