package com.bitirmeprojesi.lezzetkapisi.Repository

import android.R
import com.bitirmeprojesi.lezzetkapisi.Model.MenuItem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class MenuEditRepository {

    val db= Firebase.firestore
    val auth= Firebase.auth

    fun getMenu(menu_id: String,onError:(String)-> Unit,onSuccess:(MenuItem)-> Unit){

        db.collection("Business_Menu").document(menu_id).get().addOnSuccessListener { doc->

            val menu= MenuItem(
                menu_id = doc.id,
                business_id = auth.currentUser!!.uid ,
                food_name = doc.getString("food_name")!!,
                food_description = doc.getString("food_description")!!,
                food_price =doc.getDouble("food_price")!!,
                food_photo_url = doc.getString("food_photo_url")!!,
                category_ids = doc.get("category_ids") as List<Int>?:emptyList(),
                averageLike = doc.getDouble("averageLike")!!,
                count_command = doc.getDouble("count_command")!!.toInt(),
                createdDate = doc.getTimestamp("createdDate")!!
            )
            onSuccess(menu)

        }.addOnFailureListener {
            onError("Veri çekilirken bir hata oluştu")
        }
    }

    fun editMenu(menu_id:String,food_name:String,food_description:String,food_price: String,onError: (String) -> Unit,onSuccess: () -> Unit){
        val food_price_d=food_price.toDoubleOrNull()
        if (food_name!="" || food_description!="" || food_price!=null){
            onError("Bütün alanları doldurmanız gereklidir")
            return
        }
        val update_menu=hashMapOf<String,Any>(
            "food_name" to food_name,
            "food_description" to food_description,
            "food_price" to food_price_d!!
        )

        db.collection("Business_Menu").document(menu_id).set(update_menu).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onError("Güncelleme yapılamadı bir hata oluştu.Lütfen tekrar deneyiniz.")
        }








    }







}