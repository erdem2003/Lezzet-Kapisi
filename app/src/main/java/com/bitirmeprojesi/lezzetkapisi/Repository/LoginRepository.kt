package com.bitirmeprojesi.lezzetkapisi.Repository

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class LoginRepository{

    private val auth= Firebase.auth
    private val db= Firebase.firestore

    fun login(email:String,password:String,onSucces:(uid: String)->Unit,onError:(String)-> Unit){

        auth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                val uid=auth.currentUser?.uid
                if(uid!=null){
                    onSucces(uid)
                }
            }.addOnFailureListener {
                onError("Giriş başarısız.Kullanıcı adı ve şifre eşleşmiyor.")
            }

    }

    fun checkUserType(
        uid: String,
        onUser:()-> Unit,
        onBusiness:()->Unit,
        onError:()-> Unit
    ){
        db.collection("Users").document(uid).get()
            .addOnSuccessListener { document->
                if(document.exists()){
                    onUser()
                }else{
                    onBusiness()
                }
            }.addOnFailureListener {
                onError()
            }
    }


}