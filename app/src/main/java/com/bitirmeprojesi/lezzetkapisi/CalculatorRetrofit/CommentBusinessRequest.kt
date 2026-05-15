package com.bitirmeprojesi.lezzetkapisi.CalculatorRetrofit

data class CommentBusinessRequest(
    val sender_id:String,
    val business_id: String,
    val comment: String
)
