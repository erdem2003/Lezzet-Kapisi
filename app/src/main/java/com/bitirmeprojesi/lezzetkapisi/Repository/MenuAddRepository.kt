package com.bitirmeprojesi.lezzetkapisi.Repository


import android.net.Uri
import android.util.Log
import com.bitirmeprojesi.lezzetkapisi.Model.Menu
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await


class MenuAddRepository {

    private val db= Firebase.firestore
    private val auth= Firebase.auth
    private val storage= Firebase.storage
    val timestamp = com.google.firebase.Timestamp.now()

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

    fun menuAdd(food_name:String, food_description:String, food_price: Double, foodPhoto: Uri, categoryIdList:List<Int>,
                onError:(String)-> Unit, onSucces:()->Unit){

        if (auth.currentUser==null){
            onError("Kaynak dosyalarında hata var ! Lütfen uygulamayı yeniden yükleyiniz")
            return
        }

        val storageRef=storage.reference
            .child("menu_photos")
            .child(auth.currentUser!!.uid)
            .child("${System.currentTimeMillis()}.jpg")

        storageRef.putFile(foodPhoto)
            .addOnSuccessListener {

                //Upload başarılıysa
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl->

                    val menuData= Menu(
                        business_id = auth.currentUser!!.uid,
                        food_name=food_name,
                        food_description=food_description,
                        food_price=food_price,
                        food_photo_url = downloadUrl.toString(),
                        category_ids=categoryIdList

                    )

                    db.collection("Business_Menu")
                        .add(menuData)
                        .addOnSuccessListener {
                            onSucces()

                        }.addOnFailureListener {
                            storageRef.delete()
                            onError("Menuyu eklerken beklenmeyen hata oluştu.")

                        }
                }.addOnFailureListener {
                    storageRef.delete() // Upload'ı silsin.
                    onError("Beklenmeyen Hata")
                }



            }.addOnFailureListener {e->
                onError(e.message!!)
            }





    }









}