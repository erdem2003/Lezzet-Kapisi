package com.bitirmeprojesi.lezzetkapisi.Repository

import android.util.Log
import com.bitirmeprojesi.lezzetkapisi.Model.BusinessInfo
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class BusinessPageRepository {

    val auth= Firebase.auth
    val db= Firebase.firestore

    //İsletme infolarını al
    fun getBusinessInfo(business_id: String,onError:(String)->Unit,onSuccess:(BusinessInfo)-> Unit){ //İşletmeyi sürekli açık şekilde dinlemeliyim.
        db.collection("Business").document(business_id).addSnapshotListener { doc,error->

            if (error!=null){
                onError("İşletme sayfasına giriş yapamadık lütfen tekrar deneyiniz.")
                return@addSnapshotListener
            }
            val businessInfo=doc?.toObject(BusinessInfo::class.java)

            if (businessInfo == null) {
                onError("İşletme bulunamadı.")
                return@addSnapshotListener
            }

            Log.d("BusinessPageRepository",businessInfo.business_name)
            Log.d("BusinessPageRepository",businessInfo.business_id)
            Log.d("BusinessPageRepository",businessInfo.city)

            onSuccess(businessInfo) //Eğer işletme bilgisini başarılı bir şekilde çekiyorsa state'e atılacak bu viewModelde
        }
    }

    //Menu listele
    fun getMenuList(business_id: String){

        //db.collection()



    }


}