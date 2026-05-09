package com.bitirmeprojesi.lezzetkapisi.ViewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.bitirmeprojesi.lezzetkapisi.Model.MenuItem
import com.bitirmeprojesi.lezzetkapisi.Repository.MenuEditRepository

class MenuEditViewModel: ViewModel() {

    val menu= mutableStateOf<MenuItem?>(null)
    val error_message=mutableStateOf<String>("")
    val success_message=mutableStateOf<String>("")
    val isLoading=mutableStateOf<Boolean>(true)


    val repo= MenuEditRepository()

    fun showMenu(menu_id: String){
        isLoading.value=true

        repo.getMenu(menu_id,
            onError = { error_it->
                error_message.value=error_it
                isLoading.value=false
            },
            onSuccess = { it->
                menu.value=it
                isLoading.value=false
            })
    }

    fun editMenu(menu_id: String,food_name: String,food_description:String,food_price: String,active: Boolean){
        isLoading.value=true
        repo.editMenu(menu_id = menu_id,food_name,food_description,food_price,active,
            onError = { error_it->
                error_message.value=error_it
                isLoading.value=false
            },
            onSuccess = {
                success_message.value="Degisiklikler başarıyla kaydedildi"
                showMenu(menu_id)
            })


    }




}