package com.bitirmeprojesi.lezzetkapisi.ViewModels

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitirmeprojesi.lezzetkapisi.Model.Menu
import com.bitirmeprojesi.lezzetkapisi.Model.MenuItem
import com.bitirmeprojesi.lezzetkapisi.Repository.MenuAddRepository
import com.bitirmeprojesi.lezzetkapisi.Repository.MenuViewRepository
import kotlinx.coroutines.launch

class MenuViewViewModel : ViewModel() {

    val menuList= mutableStateOf<List<MenuItem>>(emptyList())

    val repo= MenuViewRepository()
    val menuAddRepo= MenuAddRepository()

    val errorMessage=mutableStateOf<String>("")
    val isLoading=mutableStateOf<Boolean>(true)

    val menuid_foodCategory= mutableStateMapOf<String, List<String>>()



    fun menuListController(){ //MenuListesini çeker ve Menulerin kategorilerini hazır hale getirir.
        viewModelScope.launch {
            repo.getMenuList(onError = { hataMessage->
                errorMessage.value=hataMessage
            },  onSucces = { menuList_it->
                menuList.value=menuList_it


            })

            val id_foodname_pairs=menuAddRepo.getFoodCollectionInfo()

            for (menu in menuList.value){
                val list=repo.getCategoryFromList(menu.category_ids,id_foodname_pairs)
                menuid_foodCategory.put(menu.menu_id,list)
            }
            isLoading.value=false
        }
    }

    fun clearState(){
        menuList.value=emptyList<MenuItem>()
        errorMessage.value=""
    }





}