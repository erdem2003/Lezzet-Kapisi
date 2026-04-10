package com.bitirmeprojesi.lezzetkapisi.Repository

import androidx.compose.ui.hapticfeedback.HapticFeedback
import com.google.android.gms.common.util.UidVerifier
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class StartRepository {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    fun getCurrentUser() = auth.currentUser

    fun checkUserType(
        onUser: () -> Unit,
        onBusiness: () -> Unit,
        onError: () -> Unit
    ) {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            onError()
            return
        }

        val uid = currentUser.uid

        db.collection("Users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    onUser()
                } else {
                    onBusiness()
                }
            }
            .addOnFailureListener {
                onError()
            }
    }
}