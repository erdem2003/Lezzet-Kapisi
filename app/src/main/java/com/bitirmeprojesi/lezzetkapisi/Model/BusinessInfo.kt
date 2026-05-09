package com.bitirmeprojesi.lezzetkapisi.Model

import com.google.firebase.Timestamp

data class BusinessInfo(

    val business_id: String = "",
    val business_name: String = "",
    val city: String = "",
    val average_star: Double = 0.0,
    val count_comments: Int = 0,
    val created_date: Timestamp = Timestamp.now(),
    val description: String = "",
    val email: String = "",
    val profile_photo: String = ""

)