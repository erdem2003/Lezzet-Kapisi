package com.bitirmeprojesi.lezzetkapisi.Screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun UserFeedScreen(navController: NavController){
    Text("User Screendeyiz.")

    navController.navigate("search")

}