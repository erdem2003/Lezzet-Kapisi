package com.bitirmeprojesi.lezzetkapisi.Screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.bitirmeprojesi.lezzetkapisi.ViewModels.BusinessPageViewModel

@Composable
fun BusinessPageScreen(navController: NavController,business_id: String,businessPageViewModel: BusinessPageViewModel){

    if (business_id==""){
       Text("İşletme bilgileri yüklenemedi bir hata oluştu!")
        return
    }

    Text("Merhaba business"+ business_id.toString())



}