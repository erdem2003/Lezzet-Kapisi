package com.bitirmeprojesi.lezzetkapisi.Model

import com.google.firebase.Timestamp

data class User(
    val city: String = "",
    val user_id: String = "",
    val created_date: Timestamp = Timestamp.now(),
    val email: String = "",
    val gender: String = "",
    val profile_photo: String = "",
    val username: String = ""
): User_Or_Business
