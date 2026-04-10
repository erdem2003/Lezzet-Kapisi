package com.bitirmeprojesi.lezzetkapisi.ViewModels


import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.bitirmeprojesi.lezzetkapisi.Model.City
import com.bitirmeprojesi.lezzetkapisi.Repository.RegisterRepository

class RegisterViewModel : ViewModel() {

    private val repository = RegisterRepository()

    // UI'ın gözlemlediği state'ler
    val errorMessage = mutableStateOf("")
    val isLoading = mutableStateOf(false)
    val cities = mutableStateListOf<City>()

    fun registerUser(
        email: String,
        password: String,
        username: String,
        city: String,
        gender: String,
        photoUri: Uri?,
        onSuccess: () -> Unit
    ) {
        // Boş alan kontrolü
        if (email.isEmpty() || password.isEmpty() || username.isEmpty() || city.isEmpty()) {
            errorMessage.value = "Lütfen bütün alanları doldurunuz"
            return
        }

        isLoading.value = true

        repository.registerUser(
            email = email,
            password = password,
            username = username,
            city = city,
            gender = gender,
            photoUri = photoUri,
            onSuccess = {
                isLoading.value = false
                errorMessage.value = ""
                onSuccess()
            },
            onError = { message ->
                isLoading.value = false
                errorMessage.value = message
            }
        )
    }

    fun registerBusiness(
        email: String,
        password: String,
        businessName: String,
        description: String,
        city: String,
        photoUri: Uri?,
        onSuccess: () -> Unit
    ) {
        // Boş alan kontrolü
        if (email.isEmpty() || password.isEmpty() || businessName.isEmpty() ||
            description.isEmpty() || city.isEmpty()) {
            errorMessage.value = "Lütfen bütün alanları doldurunuz"
            return
        }

        // Fotoğraf kontrolü — işletme için zorunlu
        if (photoUri == null) {
            errorMessage.value = "İşletme fotoğrafı zorunludur"
            return
        }

        isLoading.value = true

        repository.registerBusiness(
            email = email,
            password = password,
            businessName = businessName,
            description = description,
            city = city,
            photoUri = photoUri,
            onSuccess = {
                isLoading.value = false
                errorMessage.value = ""
                onSuccess()
            },
            onError = { message ->
                isLoading.value = false
                errorMessage.value = message
            }
        )
    }

    fun getCities() {
        repository.getCities(
            onSuccess = { cityList ->
                cities.clear()
                cities.addAll(cityList)
            },
            onError = { message ->
                errorMessage.value = message
            }
        )
    }



}