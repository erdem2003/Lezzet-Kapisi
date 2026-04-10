package com.bitirmeprojesi.lezzetkapisi.Screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.bitirmeprojesi.lezzetkapisi.ViewModels.StartViewModel

@Composable
fun StartScreen(navController: NavController,startViewModel: StartViewModel){

    LaunchedEffect(Unit) {
        startViewModel.routingScreen(
            goToUserFeed     = { navController.navigate("user_feed")     { popUpTo("start") { inclusive = true } } },
            goToLogin        = { navController.navigate("login")         { popUpTo("start") { inclusive = true } } },
            goToBusinessFeed = { navController.navigate("business_feed") { popUpTo("start") { inclusive = true } } }
        )
    }

    // ekranda sadece loading göster
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }


}