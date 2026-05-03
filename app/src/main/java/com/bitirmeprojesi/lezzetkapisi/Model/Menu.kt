package com.bitirmeprojesi.lezzetkapisi.Model

import com.google.firebase.Timestamp

data class Menu(

    val business_id: String="",
    val food_name: String= "",
    val food_description:String= "",
    val food_price: Double= 0.0,
    val food_photo_url: String= "",
    val category_ids: List<Int> = emptyList(),
    val averageLike: Double=0.0,
    val count_command: Int=0,
    val createdDate:Timestamp=com.google.firebase.Timestamp.now()

)
