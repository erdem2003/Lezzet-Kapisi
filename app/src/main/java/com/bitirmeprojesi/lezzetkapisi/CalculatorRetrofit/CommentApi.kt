package com.bitirmeprojesi.lezzetkapisi.CalculatorRetrofit

import retrofit2.http.Body
import retrofit2.http.POST

interface CommentApi {

    @POST("/send_message")
    suspend fun sendMessage(
        @Body message: MessageRequest
    ): MessageResponse


}