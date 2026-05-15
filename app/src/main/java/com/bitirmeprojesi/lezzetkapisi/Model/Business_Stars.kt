package com.bitirmeprojesi.lezzetkapisi.Model

import com.google.firebase.Timestamp

data class Business_Stars(
    val star_id: String="",
    val business_id: String="",
    val calculate: Boolean=false,
    val created_date: Timestamp= Timestamp.now(),
    val sender_id: String="",
    val star_value: Double=0.0
){

}
