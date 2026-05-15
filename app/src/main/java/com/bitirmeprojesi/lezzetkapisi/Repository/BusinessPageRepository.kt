package com.bitirmeprojesi.lezzetkapisi.Repository

import android.util.Log
import com.bitirmeprojesi.lezzetkapisi.CalculatorRetrofit.CalculatorClient_Instance
import com.bitirmeprojesi.lezzetkapisi.CalculatorRetrofit.CommentBusinessRequest
import com.bitirmeprojesi.lezzetkapisi.CalculatorRetrofit.StarBusinessRequest
import com.bitirmeprojesi.lezzetkapisi.Model.BusinessInfo
import com.bitirmeprojesi.lezzetkapisi.Model.Business_Comment
import com.bitirmeprojesi.lezzetkapisi.Model.Business_Stars
import com.bitirmeprojesi.lezzetkapisi.Model.Menu
import com.bitirmeprojesi.lezzetkapisi.Model.User
import com.bitirmeprojesi.lezzetkapisi.Model.User_Or_Business
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.getField
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.tasks.await
import kotlin.String

class BusinessPageRepository {

    val auth = Firebase.auth
    val db   = Firebase.firestore



    suspend fun getBusinessInfoController(
        business_id: String,
        onError: (String) -> Unit,
        onSuccess: (BusinessInfo) -> Unit
    ) {
        try {
            val doc = db.collection("Business")
                .document(business_id)
                .get()
                .await()

            val businessInfo = doc.toObject(BusinessInfo::class.java)

            if (businessInfo == null) {
                onError("İşletme bulunamadı.")
                return
            }

            Log.d("BusinessPageRepository", businessInfo.business_name)
            Log.d("BusinessPageRepository", businessInfo.business_id)
            Log.d("BusinessPageRepository", businessInfo.city)

            onSuccess(businessInfo)

        } catch (e: Exception) {
            onError("İşletme sayfasına giriş yapamadık lütfen tekrar deneyiniz.")
        }
    }

    suspend fun getMenuListController(
        business_id: String,
        onError: (String) -> Unit,
        onSuccess: (List<Menu>) -> Unit
    ) {
        try {
            val snapshot = db.collection("Business_Menu")
                .whereEqualTo("business_id", business_id)
                .get()
                .await()

            val menuList = snapshot.toObjects(Menu::class.java)

            for (doc in snapshot.documents) {
                Log.d("BusinessPageRepository", doc.getField("menu_id") ?: "")
            }

            onSuccess(menuList)

        } catch (e: Exception) {
            onError("Restorana ait menüler çekilirken bir sıkıntı oluştu lütfen tekrar deneyiniz.")
        }
    }

    suspend fun getCommentList(business_id: String,onError: (String) -> Unit,onSuccess: (List<Business_Comment>) -> Unit){

        try {
            val docs=db.collection("Business_comments").whereEqualTo("business_id",business_id).get().await()
            val list=docs.toObjects<Business_Comment>()
            onSuccess(list)
            return
        }catch (e: Exception){
            onError("Yorumlar çekilirken bir hata oluştu.")
            return
        }




    }

    suspend fun sendMessage(business_id: String,comment: String,onError: (String) -> Unit,onSuccess:(String, Business_Comment)-> Unit){
        if(auth.currentUser?.uid==null){
            onError("Mesaj yüklenemedi . Kullanıcı tarafında bir sorun var lütfen uygulamanın önbelleğini temizleyiniz.")
        }else{
            try {
                val response= CalculatorClient_Instance.calculatorBusinessApi.sendMessage(
                    CommentBusinessRequest(
                        sender_id= auth.currentUser!!.uid,
                        business_id= business_id,
                        comment=comment
                    )
                )
                if(response.status==true){
                    val doc=db.collection("Business_comments").whereEqualTo("comment_id",response.comment_id).get().await().firstOrNull()
                    if(doc!=null){
                        val business_comment=doc.toObject(Business_Comment::class.java)
                        onSuccess("Mesaj başarıyla yüklendi",business_comment)
                    }else{
                        onError("Mesajınız yüklendi beklenmedik hata oluştu lütfen sayfayı tekrardan yenileyiniz")

                    }

                }else{
                    onError("Mesaj yüklenirken bir hata oluştu lütfen tekrar deneyiniz")
                }

            }catch (e: Exception){
               onError("Mesaj yüklenirken bir hata oluştu")
            }

        }




    }

    suspend fun getUserInfo(user_id: String, onError: (String) -> Unit,onSuccess: (User_Or_Business)-> Unit) {

        var docs = db.collection("Users").whereEqualTo("user_id", user_id).get().await().documents.firstOrNull()

        if (docs==null) {//docs boş ise user değilmiş
            docs = db.collection("Business").whereEqualTo("business_id", user_id).get().await().documents.firstOrNull()
            if (docs==null) {//docs hala boş ise hatalı
                onError("Veriler çekilirken beklenmeyen hata oluştu. Lütfen tekrar deneyiniz.")
                return
            }
            //Business
            val business=docs.toObject(BusinessInfo::class.java)
            onSuccess(business!!)


        }else{ //User demek
            if (docs==null) {//docs hala boş ise hatalı
                onError("Veriler çekilirken beklenmeyen hata oluştu. Lütfen tekrar deneyiniz.")
                return
            }
            // User
            val user=docs.toObject(User::class.java)
            onSuccess(user!!)
        }
    }

    suspend fun getBusinessStarForUser(business_id:String,onError: (String) -> Unit,onSuccess:(Business_Stars)->Unit){
        //İlgili kullanıcının yıldızını çekeceğiz eğer varsa tabiki :)
        val user_id=auth.currentUser!!.uid
        val docs=db.collection("Business_stars").whereEqualTo("sender_id",user_id).whereEqualTo("business_id",business_id).get().await()

        if (docs.isEmpty){
            return
        }

        val star_doc=docs.documents.get(0)
        val star_data=star_doc.toObject(Business_Stars::class.java)
        if (star_data!=null){
            onSuccess(star_data)
            //Buraya return gelebilir mi bak ? ! .
        }else{
            onError("Yıldız bilgisi çekilirken hata oluştu ")
        }

    }

    suspend fun sendBusinessStar(business_id: String, starvalue: Double, onError: (String) -> Unit, onSuccess: (Business_Stars) -> Unit){

        val response=CalculatorClient_Instance.calculatorBusinessApi.sendStar(
            StarBusinessRequest(
                 sender_id=auth.currentUser!!.uid,
                 business_id=business_id,
                 star_value=starvalue,
            )
        )
        if (response.isSuccessful) {
            val body = response.body()  // StarBusinessResponse objesi
            if (body != null) {
                val status = body.status  // body içindeki field'lara böyle erişirsin
                val star_id=body.star_id

                val docs=db.collection("Business_stars").document(star_id).get().await()
                val business_star=docs.toObject(Business_Stars::class.java)
                onSuccess(business_star!!)
            }
        } else {
            onError("Yıldız puanınız gönderilemedi")
        }

    }






}