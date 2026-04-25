package com.bitirmeprojesi.lezzetkapisi.MyNavHost

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bitirmeprojesi.lezzetkapisi.Screens.BusinessFeedScreen
import com.bitirmeprojesi.lezzetkapisi.Screens.RegisterScreen
import com.bitirmeprojesi.lezzetkapisi.Screens.StartScreen
import com.bitirmeprojesi.lezzetkapisi.Screens.UserFeedScreen
import com.bitirmeprojesi.lezzetkapisi.ViewModels.RegisterViewModel
import com.bitirmeprojesi.lezzetkapisi.ViewModels.StartViewModel
import com.erdemkilic.bitirme_projesi.Screens.LoginScreen
import com.erdemkilic.bitirme_projesi.ViewModels.LoginViewModel


@Composable
fun MyNavHost(navController: NavHostController){

    val startDestination="start"

    NavHost(navController=navController,startDestination=startDestination){

        composable("start") {
            val startViewModel: StartViewModel= viewModel()
            StartScreen(navController,startViewModel)
        }

        composable("login") {
            val loginViewModel: LoginViewModel=viewModel()
            LoginScreen(navController,loginViewModel)
        }

        composable("register") {
            val registerViewModel: RegisterViewModel=viewModel()
            RegisterScreen(navController,registerViewModel)
        }

        composable("business_feed") {
            BusinessFeedScreen(navController)
        }
        composable("search") {

        }
        composable("chatbot") {

        }
        composable("business_menu_add") {

        }
        composable("business_menu_view") {

        }
        composable("settings") {

        }
        composable("user_feed") {

            UserFeedScreen()
        }



    }

}