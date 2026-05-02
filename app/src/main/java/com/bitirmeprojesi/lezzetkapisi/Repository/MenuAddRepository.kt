package com.bitirmeprojesi.lezzetkapisi.Repository


import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await


class MenuAddRepository {

    private val db= Firebase.firestore


    suspend fun getFoodCollectionInfo(): HashMap<Int, String>{

        val map = hashMapOf<Int, String>()

        val documents = db.collection("Food")
            .get()
            .await()  // ← Firebase bitene kadar bekle

        for (document in documents) {
            val food_id = document.getLong("food_id")?.toInt()
            val food_name = document.getString("food_name")
            if (food_name != null && food_id != null) {
                map[food_id] = food_name
            }
        }

        Log.d("YOLO", "Map dolu mu: $map")
        return map  //  Artık dolu gelir

    }









}