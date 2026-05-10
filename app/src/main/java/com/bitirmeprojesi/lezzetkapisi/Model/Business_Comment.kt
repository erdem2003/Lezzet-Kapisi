package com.bitirmeprojesi.lezzetkapisi.Model

import com.google.firebase.Timestamp

data class Business_Comment(
    val comment_id: String="",
    val sender_id: String="",
    val business_id: String="",
    val comment: String="",
    val created_date: Timestamp= Timestamp.now(),

){

}
