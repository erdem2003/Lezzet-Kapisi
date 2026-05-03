package com.bitirmeprojesi.lezzetkapisi.Repository

import android.util.Log
import com.bitirmeprojesi.lezzetkapisi.Model.Menu
import com.bitirmeprojesi.lezzetkapisi.Model.MenuItem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

class MenuViewRepository {

    val db= Firebase.firestore
    val auth= Firebase.auth
    val storage= Firebase.storage

    val repo: MenuAddRepository= MenuAddRepository()

    suspend fun getMenuList(onError:(String)-> Unit,onSucces:(List<MenuItem>)-> Unit){

        if (auth.currentUser?.uid==null){
            onError("Beklenmeyen hata.MenuView sayfası.")
            return
        }

        val uid=auth.currentUser!!.uid
        try {
            val docs=db.collection("Business_Menu").whereEqualTo("business_id",uid)
            //.orderBy("createdDate", Query.Direction.DESCENDING)
            .get().await()
            val menuList=mutableListOf<MenuItem>()
            for (doc in docs){
                val menu= MenuItem(
                    menu_id = doc.id,
                    business_id = uid,
                    food_name = doc.getString("food_name")!!,
                    food_description = doc.getString("food_description")!!,
                    food_price =doc.getDouble("food_price")!!,
                    food_photo_url = doc.getString("food_photo_url")!!,
                    category_ids = doc.get("category_ids") as List<Int>?:emptyList(),
                    averageLike = doc.getDouble("averageLike")!!,
                    count_command = doc.getDouble("count_command")!!.toInt(),
                    createdDate = doc.getTimestamp("createdDate")!!
                )
                menuList.add(menu)

            }
            onSucces(menuList)
        }catch (e: Exception){
            onError(e.message.toString()+" veriler çekilirken sıkıntı oluştu.")
            return
        }
    }



    fun getCategoryFromList(list:List<Int>,id_foodname_pairs: HashMap<Int, String>):List<String>{
        Log.d("MenuViewRepository","Bütün elemanları aldık.")

        val categoryList=mutableListOf<String>()
        for (i in list){
            Log.d("MenuViewRepository","Bütün elemanları aldık.")
            categoryList.add(id_foodname_pairs.get(i)!!)
        }
        return categoryList
    }

    fun deleteMenu(menuId: String,onError:(String)-> Unit,onSucces:()-> Unit){ // ikisinden birisinin çalışmadıgı duruma daha sonra gel bak.Burada sorun oluşabilir
        val storageRef=storage.reference
            .child("menu_photos")
            .child("${auth.uid}")
            .child("$menuId.jpg")

        storageRef.delete().addOnSuccessListener{
            db.collection("Business_Menu").document(menuId).delete().addOnSuccessListener {
                onSucces()

            }.addOnFailureListener {
                onError("Silme sırasında bir hata oluştu lütfen tekrar deneyiniz.")
            }
        }.addOnFailureListener {
            onError("Silme sırasında bir hata oluştu lütfen tekrar deneyiniz.")
        }



    }





}