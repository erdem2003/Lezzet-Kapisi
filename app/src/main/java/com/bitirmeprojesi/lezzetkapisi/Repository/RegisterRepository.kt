package com.bitirmeprojesi.lezzetkapisi.Repository

import android.net.Uri
import android.util.Log
import com.bitirmeprojesi.lezzetkapisi.Model.City
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage


class RegisterRepository {

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val storage = Firebase.storage

    fun registerUser(
        email: String,
        password: String,
        username: String,
        city: String,
        gender: String,
        photoUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty() || city.isEmpty()) {
            onError("Lütfen bütün alanları doldurunuz")
            return
        }

        checkUsername(username,
            onAvailable = {
                // Username müsaitse kayda devam et
                createUserWithAuth(email, password, username, city, gender, photoUri, onSuccess, onError)
            },
            onTaken = {
                onError("Bu kullanıcı adı zaten alınmış")
            },
            onError = { errorMsg ->
                onError(errorMsg)
            }
        )
    }

    private fun createUserWithAuth(
        email: String,
        password: String,
        username: String,
        city: String,
        gender: String,
        photoUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnFailureListener { exception ->
                onError(handleAuthError(exception))
            }
            .addOnSuccessListener {
                val uid = auth.currentUser!!.uid
                val storageRef = storage.reference.child("profile_photos/$uid.jpg")

                if (photoUri != null) {
                    uploadAndSaveUser(uid, email, username, city, gender, photoUri, storageRef, onSuccess, onError)
                } else {
                    val defaultPath = if (gender == "male") "defaults/default_erkek.jpg" else "defaults/default_kadin.jpg"
                    storage.reference.child(defaultPath).downloadUrl
                        .addOnSuccessListener { defaultUrl ->
                            saveUserToFirestore(uid, email, username, city, gender, defaultUrl.toString(), storageRef, onSuccess, onError)
                        }
                        .addOnFailureListener {
                            Log.e("REGISTER", "Default fotoğraf alınamadı, boş URL ile devam ediliyor.")
                            auth.currentUser?.delete()
                            onError("Beklenmedik hata oluştu lütfen tekrar deneyiniz")
                        }
                }
            }
    }

    private fun uploadAndSaveUser(
        uid: String,
        email: String,
        username: String,
        city: String,
        gender: String,
        photoUri: Uri,
        storageRef: com.google.firebase.storage.StorageReference,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        storageRef.putFile(photoUri)
            .continueWithTask { storageRef.downloadUrl }
            .addOnFailureListener {
                Log.e("REGISTER", "Storage yükleme başarısız. Auth siliniyor.")
                auth.currentUser?.delete()
                onError("Fotoğraf yüklenemedi")
            }
            .addOnSuccessListener { downloadUrl ->
                saveUserToFirestore(uid, email, username, city, gender, downloadUrl.toString(), storageRef, onSuccess, onError)
            }
    }

    private fun saveUserToFirestore(
        uid: String,
        email: String,
        username: String,
        city: String,
        gender: String,
        photoUrl: String,
        storageRef: com.google.firebase.storage.StorageReference,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = hashMapOf(
            "email" to email,
            "username" to username,
            "city" to city,
            "gender" to gender,
            "profile_photo" to photoUrl,
            "created_date" to Timestamp.now()
        )

        db.collection("Users").document(uid).set(user)
            .addOnFailureListener {
                Log.e("REGISTER", "Firestore başarısız, rollback işlemi gerçekleştiriliyor")
                auth.currentUser?.delete()
                if (!photoUrl.contains("defaults/")) {
                    storageRef.delete()
                }

                onError("Kayıt tamamlanamadı. Lütfen tekrardan deneyiniz")

            }
            .addOnSuccessListener {
                onSuccess()
            }
    }

    fun registerBusiness(
        email: String,
        password: String,
        businessName: String,
        description: String,
        city: String,
        photoUri: Uri?,               // DEĞİŞTİRİLDİ: Uri? yapıldı. Eskiden Uri idi,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isEmpty() || password.isEmpty() || businessName.isEmpty() ||
            description.isEmpty() || city.isEmpty()) {
            onError("Lütfen bütün alanları doldurunuz")
            return
        }


        if (photoUri == null) {
            onError("İşletme fotoğrafı zorunludur")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnFailureListener { exception ->
                onError(handleAuthError(exception))
            }
            .addOnSuccessListener {
                val uid = auth.currentUser!!.uid
                val storageRef = storage.reference.child("business_photos/$uid.jpg")

                storageRef.putFile(photoUri)
                    .continueWithTask { storageRef.downloadUrl }
                    .addOnFailureListener {
                        Log.e("REGISTER_BUSINESS", "Storage yükleme başarısız. Auth siliniyor.")
                        auth.currentUser?.delete()
                        onError("Fotoğraf yüklenemedi")
                    }
                    .addOnSuccessListener { downloadUrl ->
                        saveBusinessToFirestore(uid, email, businessName, description, city, downloadUrl.toString(), storageRef, onSuccess, onError)
                    }
            }
    }

    private fun saveBusinessToFirestore(
        uid: String,
        email: String,
        businessName: String,
        description: String,
        city: String,
        photoUrl: String,
        storageRef: com.google.firebase.storage.StorageReference,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val business = hashMapOf(
            "email" to email,
            "business_name" to businessName,
            "description" to description,
            "city" to city,
            "profile_photo" to photoUrl,
            "created_date" to Timestamp.now(),
            "average_star" to 0.0,
            "count_comments" to 0
        )

        db.collection("Business").document(uid).set(business)
            .addOnFailureListener {
                Log.e("REGISTER_BUSINESS", "Firestore başarısız, rollback yapılıyor")
                auth.currentUser?.delete()
                storageRef.delete()
                onError("Kayıt tamamlanamadı. Lütfen tekrar deneyiniz")
            }
            .addOnSuccessListener {
                onSuccess()
            }
    }


    private fun checkUsername(
        username: String,
        onAvailable: () -> Unit,
        onTaken: () -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("Users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) onAvailable() else onTaken()
            }
            .addOnFailureListener {
                onError("Kullanıcı adı kontrol edilirken hata oluştu")
            }
    }

    private fun handleAuthError(exception: Exception?): String {
        var exceptionMessage = "Beklenmeyen bir hata oluştu."
        if (exception is FirebaseAuthException) {
            exceptionMessage = when (exception.errorCode) {
                "ERROR_EMAIL_ALREADY_IN_USE" -> "Bu email zaten kayıtlı"
                "ERROR_INVALID_EMAIL" -> "Geçersiz email formatı"
                "ERROR_WEAK_PASSWORD" -> "Şifre en az 6 karakter olmalı"
                "ERROR_NETWORK_REQUEST_FAILED" -> "İnternet bağlantısı yok"
                else -> "Kayıt sırasında hata oluştu"
            }
            Log.e("REGISTER", "Auth hatası: ${exception.errorCode}")
        }
        return exceptionMessage
    }


    fun getCities(
        onSuccess: (List<City>) -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("Cities").get()
            .addOnSuccessListener { result ->
                val cityList = result.map { doc ->
                    City(
                        plate = doc.getString("plate") ?: "",
                        city_name = doc.getString("city_name") ?: ""
                    )
                }
                onSuccess(cityList)
            }
            .addOnFailureListener {
                onError("Şehirler yüklenemedi")
            }
    }

}


