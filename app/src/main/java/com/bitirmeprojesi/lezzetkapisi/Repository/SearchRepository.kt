package com.bitirmeprojesi.lezzetkapisi.Repository

import android.util.Log
import com.bitirmeprojesi.lezzetkapisi.Model.BusinessInfo
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class SearchRepository {

    val db = Firebase.firestore

    fun searchBusinessController(
        query: String,
        selectedCity: String,
        onError: (String) -> Unit,
        onSuccess: (List<BusinessInfo>) -> Unit
    ) {
        val lowerQuery = query.lowercase().trim()

        val baseQuery = if (selectedCity == "all") {
            db.collection("Business")
                .orderBy("business_name_lower")
                .startAt(lowerQuery)
                .endAt(lowerQuery + "\uF8FF")
        } else {
            db.collection("Business")
                .whereEqualTo("city", selectedCity)
                .orderBy("business_name_lower")
                .startAt(lowerQuery)
                .endAt(lowerQuery + "\uF8FF")
        }

        baseQuery.get()
            .addOnSuccessListener { snapshot ->
                val businesses = snapshot.toObjects(BusinessInfo::class.java)
                onSuccess(businesses)
            }
            .addOnFailureListener { error->
                Log.e("searchRepo", "Hata: ${error.message}")
                onError("Veriler çekilirken hata oluştu, lütfen tekrar deneyiniz alt.")
            }
    }
}