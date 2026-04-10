package com.bitirmeprojesi.lezzetkapisi.ViewModels

import androidx.lifecycle.ViewModel
import com.bitirmeprojesi.lezzetkapisi.Repository.StartRepository

class StartViewModel : ViewModel() {

    private val repository = StartRepository()

    fun routingScreen(
        goToLogin: () -> Unit,
        goToUserFeed: () -> Unit,
        goToBusinessFeed: () -> Unit
    ) {

        val currentUser = repository.getCurrentUser()

        if (currentUser == null) {
            goToLogin()
        } else {
            repository.checkUserType(
                onUser = { goToUserFeed() },
                onBusiness = { goToBusinessFeed() },
                onError = { goToLogin() }
            )
        }
    }
}