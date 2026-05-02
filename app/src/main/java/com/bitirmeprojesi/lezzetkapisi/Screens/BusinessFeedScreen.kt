package com.bitirmeprojesi.lezzetkapisi.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.bitirmeprojesi.lezzetkapisi.Components.BusinessBottomBar

@Composable
fun BusinessFeedScreen(navController: NavHostController) {

    Scaffold(

        bottomBar = {
            BusinessBottomBar(navController)
        }
    ) { padding ->

        //Kodu yazacağımız yer

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Business Feed Screen")
        }
    }
}