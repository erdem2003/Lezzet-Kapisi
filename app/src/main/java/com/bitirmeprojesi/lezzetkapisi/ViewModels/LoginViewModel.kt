package com.erdemkilic.bitirme_projesi.ViewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.bitirmeprojesi.lezzetkapisi.Repository.LoginRepository


class LoginViewModel : ViewModel() {

    private val repository = LoginRepository()

    val errorMessage = mutableStateOf("")

    fun login(
        email: String,
        password: String,
        goToUserFeed: () -> Unit,
        goToBusinessFeed: () -> Unit
    ) {

        if (email.isEmpty() || password.isEmpty()) {
            errorMessage.value = "Lütfen tüm alanları doldurun"
            return
        }

        repository.login(
            email = email,
            password = password,
            onSucces = { uid ->

                repository.checkUserType(
                    uid = uid,
                    onUser = {
                        errorMessage.value = ""
                        goToUserFeed()
                    },
                    onBusiness = {
                        errorMessage.value = ""
                        goToBusinessFeed()
                    },
                    onError = {
                        errorMessage.value = "Beklenmeyen hata"
                    }
                )
            },
            onError = { error ->
                errorMessage.value = "Giriş başarısız: $error"
            }
        )
    }
}